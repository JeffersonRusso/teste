package br.com.orquestrator.orquestrator.infra.groovy;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;

/**
 * OrchestratorScript: Classe base para scripts Groovy.
 * Fornece acesso seguro ao banco de contexto via ContextHolder.
 */
@Slf4j
public abstract class OrchestratorScript extends Script {

    public Object get(String key) {
        return ContextHolder.reader().get(key);
    }

    public void put(String key, Object value) {
        ContextHolder.writer().put(key, value);
    }

    public void log(String message) {
        log.info("[Groovy] {}", message);
    }

    public boolean hasTag(String tag) {
        return ContextHolder.metadata().getTags().contains(tag);
    }
}
