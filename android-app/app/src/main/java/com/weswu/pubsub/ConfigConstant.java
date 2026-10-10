package com.weswu.pubsub;

public interface ConfigConstant {
    // Pub/Sub Topic in vicdemo connected to BigQuery Subscription (vicdemo.wps_analytics.events_intraday)
    String TOPIC = "wps-analytics-events";

    // Cloud Run gcpauth Token Service URL in vicdemo
    String GCP_AUTH_URL = "https://gcpauth-801959478639.us-central1.run.app/v1/token";

    // MD5 of secret_key ("4d5f1ffeadf58eb64ef59aef7e53a56r") in gcpauth/conf/gcpauth.conf
    String GCP_AUTH_TOKEN = "776f9d813e3459f55eb9290aa169669c";

    // GCP Project ID
    String PROJECT_ID = "vicdemo";
}
