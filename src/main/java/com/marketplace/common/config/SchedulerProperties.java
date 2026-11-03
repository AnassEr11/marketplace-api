package com.marketplace.common.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "marketplace.scheduler")
@Getter
@Setter
public class SchedulerProperties {
    @Min(1)
    private Integer intervalMinutes;
}
