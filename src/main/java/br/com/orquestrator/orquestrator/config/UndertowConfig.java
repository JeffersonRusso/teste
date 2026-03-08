package br.com.orquestrator.orquestrator.config;

import io.undertow.server.DefaultByteBufferPool;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * UndertowConfig: Configuração de ultra-performance para Java 21.
 * Força o uso de Virtual Threads para processamento de requisições,
 * eliminando a criação excessiva de threads nativas.
 */
@Configuration
public class UndertowConfig {

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowCustomizer() {
        return factory -> {
            // Configura o executor do Undertow para usar Virtual Threads
            factory.addDeploymentInfoCustomizers(deploymentInfo -> 
                deploymentInfo.setExecutor(Executors.newVirtualThreadPerTaskExecutor())
            );

            factory.addBuilderCustomizers(builder -> {
                // Otimização de buffer para evitar contenção
                builder.setByteBufferPool(new DefaultByteBufferPool(true, 16384, -1, 0));
                
                // Threads de IO limitadas aos cores da CPU (ideal para VTs)
                builder.setIoThreads(Runtime.getRuntime().availableProcessors());
                
                // Desativa o worker thread pool nativo (já que usamos VTs no deployment)
                builder.setWorkerThreads(1); 
            });
        };
    }
}
