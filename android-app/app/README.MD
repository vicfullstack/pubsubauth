# Introduction
Android client sample that fetches short-lived OAuth2 tokens from `gcpauth` (with local token caching and automatic refresh) and publishes GA4-formatted analytics events to Cloud Pub/Sub using **HTTP transport-layer Gzip compression (`Content-Encoding: gzip`)**, streaming directly into BigQuery via a Pub/Sub BigQuery Subscription (`--use-table-schema`).

# Configuration (`ConfigConstant.java`)
Update `ConfigConstant.java` with your `TOPIC`, `GCP_AUTH_URL`, `GCP_AUTH_TOKEN`, and `PROJECT_ID`:
- `TOPIC`: Pub/Sub topic name (e.g. `app-analytics-events`), subscribed by BigQuery Subscription `sub-app-analytics-to-bq` (`vicdemo.app_analytics.events_intraday`).
- `GCP_AUTH_URL`: `https://<CLOUD_RUN_AUTH_URL>/v1/token`
- `GCP_AUTH_TOKEN`: MD5 hex digest of `secret_key` configured in `gcpauth/conf/gcpauth.conf`.
- `PROJECT_ID`: GCP project ID (e.g. `vicdemo`).

```java
    String TOPIC = "app-analytics-events";
    String GCP_AUTH_URL = "https://gcpauth-801959478639.us-central1.run.app/v1/token";
    String GCP_AUTH_TOKEN = "776f9d813e3459f55eb9290aa169669c";
    String PROJECT_ID = "vicdemo";
```