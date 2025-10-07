package com.algaworks.algadelivery.delivery.tracking.infrastructure.http.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class CourierApiClientConfig {

    public static final String BASE_URL = "http://localhost:8081";

    @Bean
    public CourierApiClient courierApiClient(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl(BASE_URL).build();

        // Transformando o restClient na interface

        // O Adapter vai receber a interface
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return proxyFactory.createClient(CourierApiClient.class);
    }
}
