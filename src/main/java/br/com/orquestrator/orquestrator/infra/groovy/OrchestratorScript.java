package br.com.orquestrator.orquestrator.infra.groovy;

import groovy.lang.Binding;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe base para todos os scripts Groovy do orquestrador.
 */
public abstract class OrchestratorScript extends Script {
    private static final Logger log = LoggerFactory.getLogger(OrchestratorScript.class);

    public OrchestratorScript() {
        super();
    }

    public OrchestratorScript(Binding binding) {
        super(binding);
    }

    @SuppressWarnings("unchecked")
    public <T> T input(String name) {
        return (T) getBinding().getVariable(name);
    }

    public void log(String msg) {
        String nodeId = (String) getBinding().getVariable("nodeId");
        log.info("[SCRIPT:{}] {}", nodeId, msg);
    }
}
