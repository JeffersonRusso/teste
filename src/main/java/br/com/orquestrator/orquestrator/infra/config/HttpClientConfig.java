package br.com.orquestrator.orquestrator.infra.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.IntStream;

/**
 * HttpClientConfig: Performance "God Mode" com Sharding de Pools.
 * Divide a carga entre 16 pools independentes para eliminar a contenção de lock.
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public List<CloseableHttpClient> apacheHttpClientPool() {
        // Criamos 16 clientes independentes. Cada um tem seu próprio lock de pool.
        // Isso pulveriza a contenção detectada no JFR em 16x.
        return IntStream.range(0, 16)
                .mapToObj(i -> {
                    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                    // Cada pool gerencia 6k conexões (Total 16 * 6k = ~100k)
                    cm.setMaxTotal(6250);
                    cm.setDefaultMaxPerRoute(6250);

                    cm.setDefaultSocketConfig(SocketConfig.custom()
                            .setSoTimeout(Timeout.ofMilliseconds(15000))
                            .setTcpNoDelay(true)
                            .setSoLinger(Timeout.ofMilliseconds(0))
                            .build());

                    // Cast explícito para evitar erro de conversão de MinimalHttpClient para CloseableHttpClient
                    return (CloseableHttpClient) HttpClients.createMinimal(cm);
                })
                .toList();
    }
}
