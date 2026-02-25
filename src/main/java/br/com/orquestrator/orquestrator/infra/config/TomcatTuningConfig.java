package br.com.orquestrator.orquestrator.infra.config;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * TomcatTuningConfig: Otimização do Servidor para Ultra-Throughput (500k+ req/s).
 */
@Configuration
public class TomcatTuningConfig {

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerCustomizer() {
        return protocolHandler -> {
            // 1. Virtual Threads para processamento de requisições
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
            
            if (protocolHandler instanceof AbstractHttp11Protocol<?> http11) {
                // 2. Conexões e Keep-Alive
                http11.setMaxConnections(200000); // Dobramos para aguentar o churn de conexões do HTTP/1.1
                http11.setMaxKeepAliveRequests(-1); // Sem limite de requests por conexão
                http11.setKeepAliveTimeout(60000); // 60 segundos
                
                // 3. Performance de Socket
                http11.setTcpNoDelay(true);
                http11.setConnectionTimeout(20000);
                
                // No Tomcat 10+, o buffer é configurado via propriedades de endpoint se necessário,
                // mas o TcpNoDelay é o mais crítico para localhost.
            }
        };
    }
}
