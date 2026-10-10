package com.weswu.pubsub;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author weswu
 * @date 2021/5/25
 */
public class PubsubPublisher {

  public Logger logger = Logger.getLogger(getClass().getName());

  public static final MediaType JsonType = MediaType.parse("application/json; charset=utf-8");
  private static final long TOKEN_REFRESH_MARGIN_MS = 5 * 60 * 1000L; // 5 minutes buffer

  private static volatile String cachedAccessToken = null;
  private static volatile long tokenExpirationTimeMillis = 0L;

  public final OkHttpClient httpClient = new OkHttpClient();
  public final OkHttpClient gzipHttpClient = new OkHttpClient.Builder()
      .addInterceptor(new GzipRequestInterceptor())
      .build();
  public final String pubsubUrl = "https://pubsub.googleapis.com:443";

  /**
   * OkHttp Interceptor that applies transport-layer Gzip compression (Content-Encoding: gzip)
   * to the HTTP POST request body while keeping PubsubMessage.data as Base64(UTF-8 JSON).
   */
  public static class GzipRequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
      Request originalRequest = chain.request();
      if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
        return chain.proceed(originalRequest);
      }
      Request compressedRequest = originalRequest.newBuilder()
          .header("Content-Encoding", "gzip")
          .method(originalRequest.method(), gzip(originalRequest.body()))
          .build();
      return chain.proceed(compressedRequest);
    }

    private RequestBody gzip(final RequestBody body) {
      return new RequestBody() {
        @Override
        public MediaType contentType() {
          return body.contentType();
        }

        @Override
        public long contentLength() {
          return -1;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
          BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
          body.writeTo(gzipSink);
          gzipSink.close();
        }
      };
    }
  }

  public HttpRes post(String url, String data, String token) {
    return post(url, data, token, false);
  }

  public HttpRes post(String url, String data, String token, boolean enableGzip) {
    HttpRes httpRes = null;
    RequestBody body = RequestBody.create(JsonType, data);
    Request request = new Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer " + token)
        .post(body)
        .build();
    OkHttpClient client = enableGzip ? gzipHttpClient : httpClient;
    try (Response response = client.newCall(request).execute()) {
      String respBody = response.body() != null ? response.body().string() : "";
      httpRes = new HttpRes(response.code(), respBody);
    } catch (Exception ex) {
      logger.log(Level.WARNING, "HTTP POST failed: " + ex.getMessage(), ex);
    }
    return httpRes;
  }

  public synchronized String get_google_access_token() throws Exception {
    long now = System.currentTimeMillis();
    if (cachedAccessToken != null && (tokenExpirationTimeMillis - now > TOKEN_REFRESH_MARGIN_MS)) {
      return cachedAccessToken;
    }

    String access_token = null;
    TokenReq tokenReq = new TokenReq();
    tokenReq.setGuid("0548e3d23304dcc26cb855c1fb96dc53");
    tokenReq.setScope("https://www.googleapis.com/auth/pubsub");
    String tokenReqStr = Json.encode(tokenReq);
    HttpRes publishRes = post(ConfigConstant.GCP_AUTH_URL, tokenReqStr, ConfigConstant.GCP_AUTH_TOKEN, false);
    if (null != publishRes && publishRes.getCode() == 200) {
      JsonObject authResJson = JsonParser.parseString(publishRes.getData()).getAsJsonObject();
      JsonObject dataObj = authResJson.getAsJsonObject("data");
      access_token = dataObj.get("tokenValue").getAsString();
      JsonElement expElem = dataObj.get("expirationTime");
      long expMillis = now + 3600_000L;
      if (expElem != null && !expElem.isJsonNull()) {
        try {
          expMillis = expElem.getAsLong();
        } catch (Exception ignored) {
        }
      }
      cachedAccessToken = access_token;
      tokenExpirationTimeMillis = expMillis;
    }
    return access_token;
  }

  public HttpRes publish(List<PubsubMessage> messages, String topicName) {
    try {
      String accessToken = get_google_access_token();
      if (null == accessToken) {
        throw new Exception("No Access Token Found.");
      }

      String fullUrl = String.format("%s/v1/projects/%s/topics/%s:publish", pubsubUrl, ConfigConstant.PROJECT_ID,
              topicName);
      PubsubMessages pubsubMessages = new PubsubMessages(messages);
      String pubsubMessagesStr = Json.encode(pubsubMessages);
      HttpRes publishRes = post(fullUrl, pubsubMessagesStr, accessToken, true);
      if (publishRes != null && publishRes.getCode() == 401) {
        // Clear cached token and retry once on 401 Unauthorized
        cachedAccessToken = null;
        tokenExpirationTimeMillis = 0L;
        accessToken = get_google_access_token();
        if (accessToken != null) {
          publishRes = post(fullUrl, pubsubMessagesStr, accessToken, true);
        }
      }
      if (publishRes != null) {
        logger.info("http request status code = " + publishRes.getCode() + ", body = " + publishRes.getData());
      }
      return publishRes;
    } catch (Exception e) {
      logger.log(Level.WARNING, "Failed to publish message: " + e.getMessage(), e);
      return null;
    }
  }
}
