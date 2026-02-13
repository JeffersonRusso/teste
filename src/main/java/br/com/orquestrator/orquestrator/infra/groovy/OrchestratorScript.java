package br.com.orquestrator.orquestrator.infra.groovy;

import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * Classe base para todos os scripts Groovy do orquestrador.
 * Fornece métodos utilitários e acesso seguro aos dados via TaskData.
 */
public abstract class OrchestratorScript extends Script {

    public OrchestratorScript() {
        super();
    }

    public OrchestratorScript(Binding binding) {
        super(binding);
    }

    /**
     * Retorna a interface de dados da task.
     */
    public TaskData getData() {
        return (TaskData) getBinding().getVariable("data");
    }

    public ObjectMapper getJsonMapper() {
        return (ObjectMapper) getBinding().getVariable("jsonMapper");
    }
    
    /**
     * Atalho para obter um dado de entrada.
     */
    @SuppressWarnings("unchecked")
    public <T> T input(String name) {
        return (T) getData().get(name);
    }

    /**
     * Atalho para registrar um dado de saída.
     */
    public void output(String name, Object value) {
        getData().put(name, value);
    }

    /**
     * Adiciona metadados à execução da task atual.
     */
    public void addMetadata(String key, Object value) {
        getData().addMetadata(key, value);
    }

    public void log(String msg) {
        // Redireciona para o log do sistema com o contexto da task
        System.out.println(STR."[SCRIPT:\{getBinding().getVariable("__NODE_ID__")}] \{msg}");
    }
}
