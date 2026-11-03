package com.marketplace.common.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "marketplace.jwt")
@Getter
@Setter
@Validated
public class JwtProperties {
    @NotBlank(message = "jwt secret is missing, set the MARKETPLACE_JWT_SECRET environment variable")
    @Size(min = 32, message = "jwt secret must be at least 32 characters long")
    private String secret;

    @NotNull
    private Integer expirationMinutes;
}
