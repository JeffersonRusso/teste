package br.com.orquestrator.orquestrator.infra.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Component
public class StreamingJsonExtractor {

    private final ObjectMapper objectMapper;
    private final JsonFactory jsonFactory;

    public StreamingJsonExtractor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonFactory = objectMapper.getFactory();
    }

    public Map<String, Object> extractFields(InputStream inputStream, Set<String> targetPaths) {
        Map<String, Object> foundData = new HashMap<>();
        if (targetPaths == null || targetPaths.isEmpty()) return foundData;

        try (JsonParser parser = jsonFactory.createParser(inputStream)) {
            // Stack para manter o caminho atual
            Deque<String> pathStack = new ArrayDeque<>();
            
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == null) break;

                if (token == JsonToken.FIELD_NAME) {
                    String fieldName = parser.currentName();
                    String parentPath = pathStack.peek();
                    String fullPath = (parentPath == null || parentPath.isEmpty()) ? fieldName : parentPath + "." + fieldName;

                    if (targetPaths.contains(fullPath)) {
                        JsonToken valueToken = parser.nextToken(); // Move para o valor
                        Object value = readValueSafe(parser, valueToken);
                        
                        log.debug("Streaming Extractor: Path '{}' -> Valor: {}", fullPath, value);
                        foundData.put(fullPath, value);
                    } 
                    else if (isPrefixOfAnyTarget(fullPath, targetPaths)) {
                        // Se é um prefixo, precisamos entrar nele
                        pathStack.push(fullPath);
                    } 
                    else {
                        // Se não é target nem prefixo, pula
                        parser.nextToken();
                        parser.skipChildren();
                    }
                } else if (token == JsonToken.END_OBJECT || token == JsonToken.END_ARRAY) {
                    if (!pathStack.isEmpty()) {
                        pathStack.pop();
                    }
                } else if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                    // Se começou um objeto/array que não foi tratado pelo FIELD_NAME (ex: root), não faz nada específico
                    // O pathStack é gerenciado pelo FIELD_NAME e END_OBJECT
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar JSON Stream", e);
        }

        return foundData;
    }

    private Object readValueSafe(JsonParser parser, JsonToken token) throws IOException {
        if (token == JsonToken.VALUE_STRING) {
            return parser.getText();
        } else if (token == JsonToken.VALUE_NUMBER_INT) {
            return parser.getLongValue();
        } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
            return parser.getDoubleValue();
        } else if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
            return parser.getBooleanValue();
        } else if (token == JsonToken.VALUE_NULL) {
            return null;
        } else {
            // Se for Objeto ou Array, lê como árvore
            return objectMapper.readValue(parser, Object.class);
        }
    }

    private boolean isPrefixOfAnyTarget(String currentPath, Set<String> targets) {
        String prefix = currentPath + ".";
        for (String target : targets) {
            if (target.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
