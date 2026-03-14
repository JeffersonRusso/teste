package br.com.orquestrator.orquestrator.infra.cache;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;

public interface CacheEngine {
    Optional<JsonNode> get(String namespace, String key);
    void put(String namespace, String key, JsonNode value, long ttlMs);
}
