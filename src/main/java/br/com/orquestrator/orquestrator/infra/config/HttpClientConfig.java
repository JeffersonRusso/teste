package br.com.orquestrator.orquestrator.infra.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

/**
 * HttpClientConfig: Configuração centralizada do cliente HTTP.
 * Otimizado para Virtual Threads usando Apache HttpClient 5.
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient restClient(CloseableHttpClient httpClient) {
        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    @Bean
    public CloseableHttpClient apacheHttpClient() {
        // Gerenciador de conexões com pool robusto
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(5000); // Até 5k conexões simultâneas
        connectionManager.setDefaultMaxPerRoute(1000); // Até 1k por host

        connectionManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(2000))
                .setSocketTimeout(Timeout.ofMilliseconds(5000))
                .setTimeToLive(60, TimeUnit.SECONDS)
                .build());

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .disableAutomaticRetries() // Retries são feitos via Decorator
                .disableCookieManagement()
                .build();
    }
}
