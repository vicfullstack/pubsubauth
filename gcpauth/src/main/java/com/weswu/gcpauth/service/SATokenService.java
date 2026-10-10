package com.weswu.gcpauth.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.weswu.gcpauth.base.GAException;
import com.weswu.gcpauth.base.ResStatus;
import com.weswu.gcpauth.base.TokenCache;
import com.weswu.gcpauth.config.Configuration;
import com.weswu.gcpauth.entity.TokenReq;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author weswu
 * @date 2021/5/23
 */
public class SATokenService{

    private final TokenCache tokenCache = TokenCache.getInstance();
    private static final Logger log = LoggerFactory.getLogger(SATokenService.class);

    public void validateReq(String gcpAuthToken, TokenReq tokenreq) throws GAException {
        Configuration.ensureLoaded();
        if (gcpAuthToken != null && gcpAuthToken.startsWith("Bearer ") && gcpAuthToken.length() > 7) {
            String bearerToken = gcpAuthToken.substring(7).trim();
            if (!DigestUtils.md5Hex(Configuration.SECRET_KEY).equalsIgnoreCase(bearerToken)){
                throw new GAException(ResStatus.FORBIDDEN, "Invalid gcpauth token");
            }
        } else {
            throw new GAException(ResStatus.FORBIDDEN, "Invalid gcpauth token");
        }
        if (null == tokenreq || null == tokenreq.getScope() || tokenreq.getScope().trim().isEmpty()){
            throw new GAException(ResStatus.FAIL, "Scope can't be null or empty");
        }
        if (!Configuration.ALLOWED_SCOPES.contains(tokenreq.getScope().trim())){
            throw new GAException(ResStatus.FORBIDDEN, "Scope: " + tokenreq.getScope() + " is not allowed");
        }
    }

    public AccessToken getAccessToken(String gcpAuthToken, TokenReq tokenreq) throws IOException, GAException{
        Configuration.ensureLoaded();
        String impersonatedServiceAccount = Configuration.IMPERSONATED_SERVICE_ACCOUNT;
        String scope = tokenreq.getScope().trim();
        Integer lifeTime = Configuration.TOKEN_EXPIRES_SEC;

        validateReq(gcpAuthToken, tokenreq);

        // Read from cache and verify token still has > 60 seconds before expiration
        Object cached = tokenCache.get(scope);
        if (cached instanceof AccessToken) {
            AccessToken cachedToken = (AccessToken) cached;
            Date exp = cachedToken.getExpirationTime();
            if (exp == null || exp.getTime() - System.currentTimeMillis() > 60_000L) {
                log.info("Hit cached access token for scope: {}", scope);
                return cachedToken;
            }
        }

        log.info("Begin to call the GCP Auth API...");
        GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();
        log.info("Get service account cred.");

        AccessToken accessToken = null;
        if (impersonatedServiceAccount != null
                && !impersonatedServiceAccount.isEmpty()
                && !impersonatedServiceAccount.startsWith("{")) {
            try {
                List<String> delegates = null;
                ImpersonatedCredentials impersonatedCredentials =
                    ImpersonatedCredentials.newBuilder()
                        .setSourceCredentials(googleCredentials)
                        .setTargetPrincipal(impersonatedServiceAccount)
                        .setScopes(Arrays.asList(scope))
                        .setLifetime(lifeTime)
                        .setDelegates(delegates)
                        .build();

                log.info("Begin to refresh ImpersonatedCredentials token for {}.", impersonatedServiceAccount);
                impersonatedCredentials.refresh();
                log.info("End refresh the ImpersonatedCredentials token.");
                accessToken = impersonatedCredentials.getAccessToken();
            } catch (IOException ex) {
                log.warn("ImpersonatedCredentials refresh failed ({}), falling back to scoped GoogleCredentials.", ex.getMessage());
            }
        }

        if (accessToken == null) {
            GoogleCredentials scopedCredentials = googleCredentials.createScoped(Collections.singletonList(scope));
            scopedCredentials.refreshIfExpired();
            accessToken = scopedCredentials.getAccessToken();
            if (accessToken == null) {
                scopedCredentials.refresh();
                accessToken = scopedCredentials.getAccessToken();
            }
        }

        log.info("Generated access token and setup the cache.");
        tokenCache.put(scope, accessToken);
        return accessToken;
    }

}
