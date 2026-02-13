package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;

public interface TaskProvider {
    /**
     * Retorna o tipo de task que este provider suporta (ex: HTTP, GROOVY_SCRIPT).
     */
    String getType();

    /**
     * Cria uma nova instância da Task baseada na definição fornecida.
     */
    Task create(TaskDefinition def);
}
