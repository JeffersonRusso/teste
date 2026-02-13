package br.com.orquestrator.orquestrator.adapter.persistence.repository.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe base abstrata para conversores JPA de JSON.
 * Centraliza a lógica de conversão e configuração do ObjectMapper.
 *
 * @param <T> O tipo do objeto Java
 */
@Slf4j
@Converter
public abstract class AbstractJsonConverter<T> implements AttributeConverter<T, String> {

    // ObjectMapper configurado estaticamente para uso em Converters (onde injeção é difícil)
    protected static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private final TypeReference<T> typeReference;
    private final Class<T> clazz;

    protected AbstractJsonConverter(Class<T> clazz) {
        this.clazz = clazz;
        this.typeReference = null;
    }

    protected AbstractJsonConverter(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
        this.clazz = null;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (attribute == null) {
            return defaultEmptyJson();
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erro ao serializar objeto para JSON: " + attribute.getClass().getSimpleName(), e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return defaultEmptyObject();
        }

        try {
            // Caminho feliz: JSON válido
            return readValue(dbData);
        } catch (Exception e) {
            // Fallback: Tenta desescapar string (comum em H2 ou dados legados double-encoded)
            try {
                String unescaped = MAPPER.readValue(dbData, String.class);
                return readValue(unescaped);
            } catch (Exception e2) {
                log.error("Falha fatal na conversão JSON. Valor bruto: {}", dbData, e2);
                throw new IllegalArgumentException("Falha ao converter JSON do banco de dados", e2);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private T readValue(String json) throws JsonProcessingException {
        if (typeReference != null) {
            return MAPPER.readValue(json, typeReference);
        }
        return MAPPER.readValue(json, clazz);
    }

    /**
     * Define o valor padrão quando a coluna do banco está vazia/nula.
     */
    protected abstract T defaultEmptyObject();

    /**
     * Define o JSON padrão quando o objeto Java é nulo ao salvar.
     */
    protected String defaultEmptyJson() {
        return "{}";
    }
}
