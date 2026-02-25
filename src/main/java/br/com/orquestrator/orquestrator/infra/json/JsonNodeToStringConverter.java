//package br.com.orquestrator.orquestrator.infra.json;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.stereotype.Component;
//
///**
// * Conversor técnico: Transforma JsonNode em String (JSON) de forma transparente.
// */
//@Component
//@RequiredArgsConstructor
//public class JsonNodeToStringConverter implements Converter<JsonNode, String> {
//    private final ObjectMapper mapper;
//
//    @Override
//    public String convert(JsonNode source) {
//        if (source.isNull()) return null;
//        if (source.isTextual()) return source.asText();
//        try {
//            return mapper.writeValueAsString(source);
//        } catch (Exception e) {
//            return source.toString();
//        }
//    }
//}
