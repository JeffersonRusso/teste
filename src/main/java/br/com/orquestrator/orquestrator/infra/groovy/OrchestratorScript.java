package br.com.orquestrator.orquestrator.infra.groovy;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * OrchestratorScript: Classe base para todos os scripts Groovy do sistema.
 * Fornece métodos utilitários para facilitar a escrita de lógica de negócio.
 */
@Slf4j
public abstract class OrchestratorScript extends Script {

    public ExecutionContext getContext() {
        return (ExecutionContext) getBinding().getVariable("context");
    }

    public Map<String, Object> getData() {
        return getContext().getRoot();
    }

    public void log(String message) {
        log.info("[Groovy] {}", message);
    }

    public Object get(String key) {
        return getContext().get(key);
    }

    public void put(String key, Object value) {
        getContext().put(key, value);
    }
}
