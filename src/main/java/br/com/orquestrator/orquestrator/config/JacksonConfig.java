package br.com.orquestrator.orquestrator.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.JsonRecyclerPools;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        // OTIMIZAÇÃO JAVA 21 (Jackson 2.16+):
        // Usa um pool baseado em ConcurrentDeque em vez de ThreadLocal.
        // Isso resolve o problema de alocação massiva de buffers em Virtual Threads (visto no TLAB).
        JsonFactory factory = JsonFactory.builder()
                .recyclerPool(JsonRecyclerPools.newConcurrentDequePool())
                .build();
        
        ObjectMapper mapper = new ObjectMapper(factory);
        
        // Módulo Blackbird para acesso ultra-rápido a campos via bytecode
        mapper.registerModule(new BlackbirdModule());
        
        // Suporte a datas ISO 8601
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Performance e Tolerância
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        
        return mapper;
    }
}
