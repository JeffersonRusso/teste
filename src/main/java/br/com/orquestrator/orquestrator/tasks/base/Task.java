package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.Map;

/**
 * Task: O contrato atômico de processamento.
 * Agora uma interface funcional pura: Dados de Entrada -> Resultado.
 */
@FunctionalInterface
public interface Task {
    
    /**
     * Executa a lógica da tarefa sobre os inputs mapeados.
     * A configuração da tarefa deve ser injetada no momento da criação (Baking).
     */
    TaskResult execute(Map<String, DataValue> inputs);
}
