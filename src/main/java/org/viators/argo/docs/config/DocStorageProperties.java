package org.viators.argo.docs.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "argo.documentation")
@Validated
public record DocStorageProperties(
    @NotNull
    String storagePathRoot
) {
}
