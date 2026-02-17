package br.com.orquestrator.orquestrator.domain;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

/**
 * Helper para gerenciar metadados de tasks no contexto de SISTEMA.
 * Garante que metadados nunca poluam os dados de negócio.
 */
public final class TaskMetadataHelper {

    public static final String STATUS = "status";
    public static final String BODY = "body";
    public static final String ERROR = "error";

    private TaskMetadataHelper() {}

    public static void update(ExecutionContext context, String nodeId, String key, Object value) {
        context.setMeta(nodeId, key, value);
    }

    public static Object get(ExecutionContext context, String nodeId, String key) {
        return context.getMeta(nodeId, key);
    }
}
