package br.com.orquestrator.orquestrator.domain;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper para gerenciar metadados de tasks no contexto.
 * Substitui chaves planas (node_X_status) por estrutura hierárquica (X.status).
 */
public final class TaskMetadataHelper {

    public static final String STATUS = "status";
    public static final String BODY = "body";
    public static final String ERROR = "error";

    private TaskMetadataHelper() {}

    @SuppressWarnings("unchecked")
    public static void update(ExecutionContext context, String nodeId, String key, Object value) {
        Map<String, Object> meta;
        Object existing = context.get(nodeId);
        
        if (existing instanceof Map) {
            meta = (Map<String, Object>) existing;
        } else {
            // Se não existe ou não é mapa (conflito?), cria novo.
            // Se houver conflito com output de negócio, o output venceu ou perdeu?
            // Aqui assumimos que o nodeId é reservado para metadados.
            meta = new HashMap<>();
            context.put(nodeId, meta);
        }
        
        meta.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static Object get(ExecutionContext context, String nodeId, String key) {
        Object existing = context.get(nodeId);
        if (existing instanceof Map) {
            return ((Map<String, Object>) existing).get(key);
        }
        return null;
    }
}
