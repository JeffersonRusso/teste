package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.api.signal.CoreSignals;
import br.com.orquestrator.orquestrator.api.signal.Signal;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SignalSchema: Validador de topologia de sinais.
 */
public class SignalSchema {
    private final Set<String> available = new HashSet<>();

    public static SignalSchema from(List<TaskDefinition> tasks) {
        SignalSchema schema = new SignalSchema();
        schema.register(CoreSignals.RAW);
        
        if (tasks != null) {
            tasks.forEach(task -> {
                // CORREÇÃO DEMÉTER: Pede apenas os nomes dos sinais produzidos.
                task.getProducedSignalNames().forEach(schema::register);
            });
        }
        return schema;
    }

    public void register(Signal signal) {
        if (signal != null) available.add(signal.signalName());
    }

    public void register(String signalName) {
        if (signalName != null && !signalName.isBlank()) available.add(signalName);
    }

    public boolean canProvide(String signalName) {
        return signalName != null && available.contains(signalName);
    }
}
