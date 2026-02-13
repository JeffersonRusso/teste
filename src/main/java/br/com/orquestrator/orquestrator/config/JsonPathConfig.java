package br.com.orquestrator.orquestrator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.springframework.context.annotation.Bean;

import java.util.EnumSet;

@org.springframework.context.annotation.Configuration
public class JsonPathConfig {

    /**
     * Cria uma configuração global do JsonPath que reutiliza o ObjectMapper da aplicação.
     * Isso garante que configurações de data, nulos e módulos sejam respeitadas.
     */
    @Bean
    public Configuration jsonPathConfiguration(ObjectMapper objectMapper) {
        return Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider(objectMapper))
                .mappingProvider(new JacksonMappingProvider(objectMapper))
                .options(EnumSet.noneOf(Option.class)) // Adicione opções globais aqui se precisar (ex: SUPPRESS_EXCEPTIONS)
                .build();
    }
}
