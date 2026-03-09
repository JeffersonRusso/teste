package br.com.orquestrator.orquestrator.config;

import io.undertow.server.DefaultByteBufferPool;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * UndertowConfig: Configuração de ultra-performance para Java 21.
 * Destravado para suportar alta concorrência (1.5k+ RPS).
 */
@Configuration
public class UndertowConfig {

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowCustomizer() {
        return factory -> {
            // Executor de Virtual Threads para vazão máxima
            factory.addDeploymentInfoCustomizers(deploymentInfo -> 
                deploymentInfo.setExecutor(Executors.newVirtualThreadPerTaskExecutor())
            );

            factory.addBuilderCustomizers(builder -> {
                builder.setByteBufferPool(new DefaultByteBufferPool(true, 16384, -1, 0));
                builder.setIoThreads(Runtime.getRuntime().availableProcessors());
                
                // DESTRAVADO: Removemos o limite artificial de 100 threads.
                // Deixamos o Undertow gerenciar o pool nativo (padrão ~256) para aceitar conexões.
                // O processamento real acontece nas Virtual Threads acima.
            });
        };
    }
}
