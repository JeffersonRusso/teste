package br.com.orquestrator.orquestrator.config;

import io.undertow.server.DefaultByteBufferPool;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * UndertowConfig: Otimização para Virtual Threads.
 * Resolve o pinning de threads causado pelo DefaultByteBufferPool do Undertow.
 */
@Configuration
public class UndertowConfig {

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowCustomizer() {
        return factory -> factory.addBuilderCustomizers(builder -> {
            // Configura um pool de buffers global sem cache por thread (ThreadLocalCache)
            // O quarto parâmetro '0' desabilita o ThreadLocalCache, eliminando o MONITOR pinning
            // detectado em io.undertow.server.DefaultByteBufferPool$ThreadLocalCache.get.
            // Parâmetros: direct, bufferSize, maximumPoolSize, threadLocalCacheSize
            builder.setByteBufferPool(new DefaultByteBufferPool(true, 16384, -1, 0));
        });
    }
}
