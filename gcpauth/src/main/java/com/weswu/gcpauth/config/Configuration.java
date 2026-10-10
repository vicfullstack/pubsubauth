package com.weswu.gcpauth.config;

import com.weswu.gcpauth.base.Const;
import com.weswu.gcpauth.base.GAException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author weswu
 * @date 2021/05/23
 */
public class Configuration {
    static IniReader iniReader;
    private static volatile boolean loaded = false;
    public static String GOOGLE_CREDENTIALS = null;
    public static String SECRET_KEY = null;
    public static String GOOGLE_AUTH_ENDPOINT = null;
    public static int TOKEN_EXPIRES_SEC = 3600;
    public static int CACHED_TOKENS_TTL = 1800;
    public static Map<?,?> GOOGLE_CREDENTIALS_JSON = null;
    public static List<String> ALLOWED_SCOPES = null;
    public static String ALLOWED_SCOPES_URL_PREFIX = null;
    public static String IMPERSONATED_SERVICE_ACCOUNT = null;

    public static synchronized void ensureLoaded() {
        if (!loaded) {
            try {
                load();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load gcpauth configuration", e);
            }
        }
    }

    /* custom configurable options */
    public static synchronized void load() throws Exception {
        String confPath = getEnvOrDefault("GCPAUTH_CONF_FILE", Const.CONG_FILE);
        iniReader = IniReader.getInstance(confPath);
        GOOGLE_CREDENTIALS = getPropOrDefault("google_credentials", "GCPAUTH_GOOGLE_CREDENTIALS", null);
        SECRET_KEY = getPropOrDefault("secret_key", "GCPAUTH_SECRET_KEY", "4d5f1ffeadf58eb64ef59aef7e53a56r");
        GOOGLE_AUTH_ENDPOINT = getPropOrDefault("google_auth_endpoint", "GCPAUTH_GOOGLE_AUTH_ENDPOINT", "https://oauth2.googleapis.com/token");
        TOKEN_EXPIRES_SEC = Integer.parseInt(getPropOrDefault("token_expires_sec", "GCPAUTH_TOKEN_EXPIRES_SEC", "3600").trim());
        CACHED_TOKENS_TTL = Integer.parseInt(getPropOrDefault("cached_tokens_ttl", "GCPAUTH_CACHED_TOKENS_TTL", "1800").trim());
        ALLOWED_SCOPES_URL_PREFIX = getPropOrDefault("allowed_scope_url_prefix", "GCPAUTH_ALLOWED_SCOPE_URL_PREFIX", "https://www.googleapis.com/auth").trim();
        ALLOWED_SCOPES = loadAllowedScopes(getPropOrDefault("allowed_scopes", "GCPAUTH_ALLOWED_SCOPES", "pubsub"));
        IMPERSONATED_SERVICE_ACCOUNT = getPropOrDefault("impersonated_service_account", "GCPAUTH_IMPERSONATED_SA", "").trim();
        loaded = true;
    }

    private static String getEnvOrDefault(String envKey, String defaultVal) {
        String val = System.getenv(envKey);
        return (val != null && !val.trim().isEmpty()) ? val.trim() : defaultVal;
    }

    private static String getPropOrDefault(String propKey, String envKey, String defaultVal) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }
        String propVal = iniReader != null ? iniReader.getProperty(propKey) : null;
        if (propVal != null && !propVal.trim().isEmpty()) {
            return propVal.trim();
        }
        return defaultVal;
    }

    public static List<String> loadAllowedScopes(String allowedScopesStr) throws GAException {
        List<String> allowedScopes = new ArrayList<>();
        if (null == allowedScopesStr || allowedScopesStr.trim().isEmpty()) {
            throw new GAException("Configuration Error", "'allowed scopes' can't be null or empty.");
        }
        List<String> tmp_scopes = Arrays.asList(allowedScopesStr.split(","));
        for (String scope : tmp_scopes) {
            if (null != scope && !scope.trim().isEmpty()) {
                allowedScopes.add(Configuration.ALLOWED_SCOPES_URL_PREFIX + "/" + scope.trim());
            }
        }
        return allowedScopes;
    }
}
