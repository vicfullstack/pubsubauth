package com.weswu.pubsub;

import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncTaskPubsub extends AsyncTask<String, Void, Void> {
    private static final PubsubPublisher PUBLISHER = new PubsubPublisher();

    @Override
    protected Void doInBackground(String... strings) {
        try {
            List<PubsubMessage> messages = new ArrayList<>();
            Map<String, String> attributes = new HashMap<>();
            attributes.put("app_id", "com.example.analytics.app");
            attributes.put("platform", "ANDROID");
            attributes.put("sdk_version", "2.1.0");
            PubsubMessage pubsubMessage = new PubsubMessage(Json.encode(buildTestData()), attributes);
            messages.add(pubsubMessage);
            PUBLISHER.publish(messages, ConfigConstant.TOPIC);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    public static Map<String, Object> buildTestData() {
        long nowMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Map<String, Object> pubsubContent = new HashMap<>();
        pubsubContent.put("event_date", sdf.format(new Date(nowMillis)));
        pubsubContent.put("event_timestamp", nowMillis * 1000L);
        pubsubContent.put("event_name", "doc_open");
        pubsubContent.put("user_pseudo_id", UUID.randomUUID().toString());
        pubsubContent.put("platform", "ANDROID");
        pubsubContent.put("stream_id", "android_app_main");
        pubsubContent.put("time", nowMillis);
        pubsubContent.put("usage", "Test");

        List<Map<String, Object>> eventParams = new ArrayList<>();
        Map<String, Object> docTypeParam = new HashMap<>();
        docTypeParam.put("key", "doc_type");
        Map<String, Object> docTypeVal = new HashMap<>();
        docTypeVal.put("string_value", "document");
        docTypeParam.put("value", docTypeVal);
        eventParams.add(docTypeParam);
        pubsubContent.put("event_params", eventParams);

        Map<String, Object> device = new HashMap<>();
        device.put("category", "mobile");
        device.put("operating_system", "ANDROID");
        pubsubContent.put("device", device);

        Map<String, Object> appInfo = new HashMap<>();
        appInfo.put("id", "com.example.analytics.app");
        appInfo.put("version", "1.0.0");
        pubsubContent.put("app_info", appInfo);

        return pubsubContent;
    }
}
