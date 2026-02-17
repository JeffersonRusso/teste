package br.com.orquestrator.orquestrator.infra.groovy;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * Classe base para todos os scripts Groovy do orquestrador.
 */
public abstract class OrchestratorScript extends Script {

    public OrchestratorScript() {
        super();
    }

    public OrchestratorScript(Binding binding) {
        super(binding);
    }

    public ExecutionContext getContext() {
        return (ExecutionContext) getBinding().getVariable("context");
    }

    public ObjectMapper getJsonMapper() {
        return (ObjectMapper) getBinding().getVariable("jsonMapper");
    }
    
    @SuppressWarnings("unchecked")
    public <T> T input(String name) {
        return (T) getContext().get(name);
    }

    public void output(String name, Object value) {
        getContext().put(name, value);
    }

    public void addMetadata(String key, Object value) {
        String nodeId = (String) getBinding().getVariable("__NODE_ID__");
        getContext().track(nodeId, key, value);
    }

    public void log(String msg) {
        System.out.println(STR."[SCRIPT:\{getBinding().getVariable("__NODE_ID__")}] \{msg}");
    }
}
