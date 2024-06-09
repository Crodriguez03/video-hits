package com.example.videohits.config;

import java.time.Duration;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(50);
		connectionManager.setDefaultMaxPerRoute(20);

		CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();

		return builder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
				.setConnectTimeout(Duration.ofMillis(1000))
				.build();
	}
}