package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import java.util.Map;

/**
 * TaskInterceptor: Unidade de lógica linear (telemetria, erro, validação).
 */
public interface TaskInterceptor {
    TaskResult intercept(Chain chain);

    interface Chain {
        Map<String, DataValue> inputs();
        TaskResult proceed(Map<String, DataValue> inputs);
    }
}
