package br.com.orquestrator.orquestrator.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.decimal4j.immutable.Decimal2f;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;

@Configuration
public class Decimal4jConfig {

    @Bean
    public SimpleModule decimal4jModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Decimal2f.class, new Decimal2fSerializer());
        module.addDeserializer(Decimal2f.class, new Decimal2fDeserializer());
        return module;
    }

    static class Decimal2fSerializer extends JsonSerializer<Decimal2f> {
        @Override
        public void serialize(Decimal2f value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                // Escreve como número (BigDecimal) para manter precisão no JSON
                gen.writeNumber(value.toBigDecimal());
            }
        }
    }

    static class Decimal2fDeserializer extends JsonDeserializer<Decimal2f> {
        @Override
        public Decimal2f deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            // Lê como BigDecimal e converte
            BigDecimal bd = p.getDecimalValue();
            return bd == null ? null : Decimal2f.valueOf(bd);
        }
    }
}
