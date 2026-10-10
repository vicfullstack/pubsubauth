# 简介
`gcpauth` 是基于 Spring Boot (Java 17) 实现的轻量级令牌签发服务，通过 **服务账号模拟（Service Account Impersonation，`ImpersonatedCredentials`）** 为客户端签发短效、最小权限的 Google Cloud OAuth2 Access Token（`https://www.googleapis.com/auth/pubsub`）。

# 服务账号与权限配置
1. **Cloud Run 运行服务账号 (Caller SA)**（例如 `firebase-adminsdk-vyd7s@vicdemo.iam.gserviceaccount.com`）：
   - 授予角色：`Service Account Token Creator` (`roles/iam.serviceAccountTokenCreator`)
2. **被模拟的目标发布服务账号 (Impersonated Target SA)**（例如 `801959478639-compute@developer.gserviceaccount.com`）：
   - 授予角色：在目标 Pub/Sub Topic 上授予 `Pub/Sub Publisher` (`roles/pubsub.publisher`)

# 参数配置 (`conf/gcpauth.conf` 或环境变量)
编辑 `conf/gcpauth.conf`（或通过环境变量 `GCPAUTH_SECRET_KEY`、`GCPAUTH_IMPERSONATED_SA`、`GCPAUTH_ALLOWED_SCOPES`、`GCPAUTH_TOKEN_EXPIRES_SEC`、`GCPAUTH_CACHED_TOKENS_TTL` 覆盖）：
- `secret_key`：客户端与服务端约定的共享密钥，客户端请求时携带 `Authorization: Bearer <MD5(secret_key)>`。
- `impersonated_service_account`：用于模拟并签发 Pub/Sub 发布令牌的目标服务账号邮箱。

# 部署至 Cloud Run
```bash
gcloud run deploy gcpauth \
  --project=vicdemo \
  --source=. \
  --cpu=1 \
  --memory=1024Mi \
  --port=8080 \
  --region=us-central1 \
  --max-instances=2 \
  --min-instances=0 \
  --allow-unauthenticated \
  --service-account=firebase-adminsdk-vyd7s@vicdemo.iam.gserviceaccount.com
```