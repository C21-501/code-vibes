package ru.c21501.rfcservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Конфигурация RestTemplate с поддержкой PATCH метода для интеграции с Planka
 * Использует JDK HttpClient который поддерживает PATCH и имеет правильное управление соединениями
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // JDK HttpClient с поддержкой HTTP/1.1 (без keep-alive проблем)
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(10));

        return new RestTemplate(factory);
    }
}