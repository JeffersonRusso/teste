package br.com.orquestrator.orquestrator.domain.vo;

/**
 * Constraint: Regra de integridade para o banco de dados de request.
 */
@FunctionalInterface
public interface Constraint {
    /**
     * Valida uma tentativa de escrita.
     * @throws br.com.orquestrator.orquestrator.exception.PipelineException se a regra for violada.
     */
    void validate(String key, Object value, java.util.Map<String, Object> currentData);
}
