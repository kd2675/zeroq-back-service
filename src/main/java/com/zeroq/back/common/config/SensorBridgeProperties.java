package com.zeroq.back.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "zeroq.sensor.bridge")
public class SensorBridgeProperties {
    private String baseUrl = "http://localhost:20181";
    private int connectTimeoutMs = 3000;
    private int readTimeoutMs = 7000;
}
