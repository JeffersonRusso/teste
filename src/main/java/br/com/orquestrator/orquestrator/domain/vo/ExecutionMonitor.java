package br.com.orquestrator.orquestrator.domain.vo;

/**
 * Abstração para rastreio de métricas, status e erros de execução.
 */
public interface ExecutionMonitor {
    void track(String nodeId, String key, Object value);
    void setStatus(String nodeId, int code);
    void setError(String nodeId, Object error);
    Object getMeta(String nodeId, String key);
}
