package br.com.orquestrator.orquestrator.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * HttpClientConfig: Configuração de alta performance para Virtual Threads.
 * Resolve o memory leak ao centralizar o HttpClient e seu SelectorManager.
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        // Criamos um HttpClient nativo do Java 21 configurado para Virtual Threads
        HttpClient httpClient = HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor()) // Usa VTs para o processamento de rede
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        // Usamos a factory do Spring para integrar o HttpClient nativo com o RestClient
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        
        return builder
                .requestFactory(factory)
                .build();
    }
}
