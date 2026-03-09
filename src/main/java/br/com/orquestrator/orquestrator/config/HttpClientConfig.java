package br.com.orquestrator.orquestrator.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * HttpClientConfig: Configuração de Pool de Conexões de Alta Performance.
 * Destravado para 10k conexões simultâneas.
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public PoolingHttpClientConnectionManager connectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        // Configurações agressivas para 1.5k+ RPS
        manager.setMaxTotal(10000); 
        manager.setDefaultMaxPerRoute(5000); // Evita gargalo no WireMock/Go
        return manager;
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager connectionManager) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(2))
                .setResponseTimeout(Timeout.ofSeconds(10))
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .disableAutomaticRetries()
                .build();
    }

    @Bean
    public ClientHttpRequestFactory requestFactory(CloseableHttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public RestClient.Builder restClientBuilder(ClientHttpRequestFactory factory) {
        return RestClient.builder().requestFactory(factory);
    }
}
