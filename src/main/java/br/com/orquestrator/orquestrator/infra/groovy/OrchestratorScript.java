package br.com.orquestrator.orquestrator.infra.groovy;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class OrchestratorScript extends Script {

    /** Retorna o valor bruto (raw) para uso direto no script. */
    public Object get(String key) {
        return ContextHolder.reader().getRaw(key);
    }

    /** Retorna o DataValue completo se o script precisar de metadados. */
    public DataValue getDataValue(String key) {
        return ContextHolder.reader().get(key);
    }

    public void put(String key, Object value) {
        ContextHolder.writer().put(key, DataValue.of(value));
    }

    public void log(String message) {
        log.info("[Groovy] {}", message);
    }

    public boolean hasTag(String tag) {
        return ContextHolder.metadata().getTags().contains(tag);
    }
}
