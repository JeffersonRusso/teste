package br.com.orquestrator.orquestrator.core.engine.binding;

import com.fasterxml.jackson.databind.JsonNode;

public interface OutputMapper {
    JsonNode map(Object result);
}
