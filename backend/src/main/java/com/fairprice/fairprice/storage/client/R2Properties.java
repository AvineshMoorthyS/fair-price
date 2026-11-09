package com.fairprice.fairprice.storage.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "r2")
public class R2Properties {

    private String endpoint;
    private String accessKeyId;
    private String secretAccessKey;
    private String bucket;
    private String region = "auto";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = trim(endpoint);
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = trim(accessKeyId);
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = trim(secretAccessKey);
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = trim(bucket);
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = trim(region);
    }

    /**
     * IntelliJ's semicolon-separated env var field and copy-pasted secrets are prone to
     * picking up leading/trailing whitespace, which silently breaks SigV4 signing.
     */
    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
