package br.com.orquestrator.orquestrator.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class HttpClientConfig {

    private static final int CLIENT_SHARDS = 16; // Aumentado para 16 para reduzir contenção no Selector
    private final HttpClient[] httpClients = new HttpClient[CLIENT_SHARDS];
    private final AtomicInteger counter = new AtomicInteger(0);

    public HttpClientConfig() {
        // OTIMIZAÇÃO: Configura o sistema para reduzir wakeups do Selector no Windows
        System.setProperty("jdk.httpclient.selectorTimeout", "1000");

        for (int i = 0; i < CLIENT_SHARDS; i++) {
            httpClients[i] = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(2))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
        }
    }

    @Bean
    @Primary
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    @Primary
    public RestClient.Builder restClientBuilder(ClientHttpRequestFactory factory) {
        return RestClient.builder().requestFactory(factory);
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        return (uri, httpMethod) -> {
            int index = Math.abs(counter.getAndIncrement() % CLIENT_SHARDS);
            JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClients[index]);
            factory.setReadTimeout(Duration.ofSeconds(5));
            return factory.createRequest(uri, httpMethod);
        };
    }
}
