package br.com.orquestrator.orquestrator.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Configuration
public class HttpClientConfig {

    @Bean
    public List<HttpClient> javaHttpClientPool() {
        // 32 clientes é o "Sweet Spot" para 100k RPS em HTTP/1.1 no Windows
        return IntStream.range(0, 32)
                .mapToObj(i -> HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(2))
                        .followRedirects(HttpClient.Redirect.NEVER)
                        .executor(Executors.newVirtualThreadPerTaskExecutor())
                        .version(HttpClient.Version.HTTP_1_1)
                        .build())
                .toList();
    }
}
