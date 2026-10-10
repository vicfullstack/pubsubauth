# 简介
Android 客户端示例模块，展示如何从 `gcpauth` 获取短效 OAuth2 访问令牌（内置本地内存缓存与过期自动刷新），并通过 **HTTP 网络传输层 Gzip 压缩（`Content-Encoding: gzip`）** 将符合 GA4 格式的埋点事件批量发布至 Cloud Pub/Sub，经由 Pub/Sub BigQuery Subscription（`--use-table-schema`）实时流式写入 BigQuery。

# 配置说明 (`ConfigConstant.java`)
在 `ConfigConstant.java` 中配置以下常量：
- `TOPIC`：Pub/Sub Topic 名称（例如 `app-analytics-events`），绑定至 BigQuery Subscription `sub-app-analytics-to-bq`（目标表 `vicdemo.app_analytics.events_intraday`）。
- `GCP_AUTH_URL`：Cloud Run `gcpauth` 服务的令牌接口地址 `https://<CLOUD_RUN_AUTH_URL>/v1/token`。
- `GCP_AUTH_TOKEN`：`gcpauth/conf/gcpauth.conf` 中 `secret_key` 的 MD5 十六进制摘要。
- `PROJECT_ID`：GCP 项目 ID（例如 `vicdemo`）。

```java
    String TOPIC = "app-analytics-events";
    String GCP_AUTH_URL = "https://gcpauth-801959478639.us-central1.run.app/v1/token";
    String GCP_AUTH_TOKEN = "776f9d813e3459f55eb9290aa169669c";
    String PROJECT_ID = "vicdemo";
```