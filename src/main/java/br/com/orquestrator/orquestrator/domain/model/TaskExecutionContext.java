package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TaskExecutionContext: Carrega o estado durante a execução.
 */
public class TaskExecutionContext {
    private final TaskDefinition definition;
    private final Map<String, DataNode> inputs;
    private final Map<String, Object> metadata = new HashMap<>();

    public TaskExecutionContext(TaskDefinition definition, Map<String, DataNode> inputs) {
        this.definition = definition;
        this.inputs = Collections.unmodifiableMap(inputs);
    }

    public String getTaskName() { return definition.nodeId().value(); }
    public String getTaskType() { return definition.type(); }
    
    /**
     * Lei de Deméter: Atalho para política de erro.
     */
    public boolean isFailFast() {
        return definition.behavior().failFast();
    }

    public Map<String, DataNode> getInputs() { return inputs; }
    
    public DataNode getInput(String key) {
        return inputs.getOrDefault(key, null);
    }

    public Map<String, Object> getNativeInputs() {
        Map<String, Object> nativeMap = new HashMap<>();
        inputs.forEach((k, v) -> nativeMap.put(k, v.asNative()));
        return nativeMap;
    }

    public void addMetadata(String key, Object value) { metadata.put(key, value); }
    public Object getMetadata(String key) { return metadata.get(key); }
    
    // Mantido apenas para casos onde o acesso à definição completa é inevitável (ex: Interceptores dinâmicos)
    public TaskDefinition getDefinition() { return definition; }
}
