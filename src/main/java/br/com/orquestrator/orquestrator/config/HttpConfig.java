package br.com.orquestrator.orquestrator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * Configuração HTTP de ultra-performance otimizada para Java 21+.
 * Ajustada para HTTP/1.1 para evitar o erro 'too many concurrent streams' em alta carga.
 */
@Configuration
public class HttpConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                // Forçamos HTTP/1.1 para garantir que o cliente abra múltiplas conexões TCP
                // em vez de tentar multiplexar tudo em uma única conexão HTTP/2,
                // o que causa gargalo de 'concurrent streams' em orquestradores massivos.
                .version(HttpClient.Version.HTTP_1_1) 
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Bean
    public ClientHttpRequestFactory requestFactory(HttpClient httpClient) {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(5));
        return factory;
    }

    @Bean
    public RestClient.Builder restClientBuilder(ClientHttpRequestFactory factory) {
        return RestClient.builder()
                .requestFactory(factory);
    }
}
