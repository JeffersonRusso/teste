package br.com.orquestrator.orquestrator.config;

import io.undertow.server.DefaultByteBufferPool;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UndertowConfig {

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowCustomizer() {
        return factory -> factory.addBuilderCustomizers(builder -> {
            // OTIMIZAÇÃO CRÍTICA PARA VIRTUAL THREADS:
            // Desativa o cache de ThreadLocal que causa contenção massiva (SynchronizedMap).
            // Usamos um pool direto e maior para evitar context switching.
            builder.setByteBufferPool(new DefaultByteBufferPool(true, 16384, -1, 0));
            
            // Ajusta o número de threads de IO para o número de cores (ideal para VTs)
            builder.setIoThreads(Runtime.getRuntime().availableProcessors());
        });
    }
}
