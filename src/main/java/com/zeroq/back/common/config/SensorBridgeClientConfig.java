package com.zeroq.back.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SensorBridgeProperties.class)
public class SensorBridgeClientConfig {

    @Bean
    public RestClient sensorBridgeRestClient(SensorBridgeProperties sensorBridgeProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(sensorBridgeProperties.getConnectTimeoutMs());
        requestFactory.setReadTimeout(sensorBridgeProperties.getReadTimeoutMs());

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(sensorBridgeProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
