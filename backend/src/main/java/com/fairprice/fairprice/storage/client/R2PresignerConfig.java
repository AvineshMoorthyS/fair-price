package com.fairprice.fairprice.storage.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(R2Properties.class)
public class R2PresignerConfig {

    private static final Logger log = LoggerFactory.getLogger(R2PresignerConfig.class);

    @Bean
    public S3Presigner s3Presigner(R2Properties r2Properties) {
        requireResolved("r2.endpoint", "R2_ENDPOINT", r2Properties.getEndpoint());
        requireResolved("r2.access-key-id", "R2_ACCESS_KEY_ID", r2Properties.getAccessKeyId());
        requireResolved("r2.secret-access-key", "R2_SECRET_ACCESS_KEY", r2Properties.getSecretAccessKey());

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                r2Properties.getAccessKeyId(),
                r2Properties.getSecretAccessKey()
        );

        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(r2Properties.getRegion()))
                .endpointOverride(URI.create(r2Properties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();

        log.info("Configured R2 S3Presigner for endpoint={}, bucket={}, region={}",
                r2Properties.getEndpoint(), r2Properties.getBucket(), r2Properties.getRegion());

        return presigner;
    }

    /**
     * Spring leaves an unresolved {@code ${ENV_VAR}} placeholder as literal text instead of
     * failing at startup, so a blank value isn't the only failure mode worth checking for.
     */
    private void requireResolved(String property, String envVar, String value) {
        if (value == null || value.isBlank() || value.contains("${")) {
            log.error("Property {} did not resolve to a real value (got '{}'); set the {} environment variable",
                    property, value, envVar);
            throw new IllegalStateException(
                    "Missing or unresolved R2 configuration for '" + property + "'. Set the " + envVar + " environment variable.");
        }
    }
}
