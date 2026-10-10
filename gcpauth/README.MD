# Introduction
`gcpauth` is a Spring Boot token-vending service that issues short-lived, narrow-scoped OAuth2 access tokens (`https://www.googleapis.com/auth/pubsub`) via Service Account Impersonation (`ImpersonatedCredentials`, with automatic fallback to scoped Application Default Credentials for local testing).

# Service Account Setup
1. **Cloud Run Runtime Service Account** (e.g. `firebase-adminsdk-vyd7s@vicdemo.iam.gserviceaccount.com`):
   - Attached Role: `Service Account Token Creator` (`roles/iam.serviceAccountTokenCreator`)
2. **Impersonated Target Service Account** (e.g. `801959478639-compute@developer.gserviceaccount.com`):
   - Attached Role: `Pub/Sub Publisher` (`roles/pubsub.publisher` or `roles/pubsub.editor`)

# Configuration (`conf/gcpauth.conf` or Environment Variables)
Update `conf/gcpauth.conf` (or override via environment variables `GCPAUTH_SECRET_KEY`, `GCPAUTH_IMPERSONATED_SA`, `GCPAUTH_ALLOWED_SCOPES`):
- `secret_key`: Shared secret key; clients send `Authorization: Bearer <MD5(secret_key)>`.
- `impersonated_service_account`: Target service account to impersonate for Pub/Sub publishing.

# Deploy to Cloud Run
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