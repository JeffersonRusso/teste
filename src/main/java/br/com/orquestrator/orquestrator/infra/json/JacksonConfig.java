package br.com.orquestrator.orquestrator.infra.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.JsonRecyclerPools;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * JacksonConfig: Otimização de Performance e Memória.
 * 1. Blackbird: Otimiza reflection via geração de bytecode.
 * 2. SharedBoundedPool: Evita alocações massivas de BufferRecycler em Virtual Threads.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        // Configura a factory com o pool de buffers compartilhado (Essencial para Virtual Threads)
        // Isso resolve o problema de alocação detectado no TLAB (BufferRecycler.balloc).
        JsonFactory factory = JsonFactory.builder()
                .recyclerPool(JsonRecyclerPools.sharedBoundedPool())
                .build();

        // Constrói o ObjectMapper usando o builder do Spring para manter as configurações padrão
        ObjectMapper mapper = builder.factory(factory).build();
        
        // Registra o módulo Blackbird para performance de reflection
        mapper.registerModule(new BlackbirdModule());

        return mapper;
    }
}
