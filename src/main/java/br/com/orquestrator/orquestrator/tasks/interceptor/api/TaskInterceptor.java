package br.com.orquestrator.orquestrator.tasks.interceptor.api;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

/**
 * TaskInterceptor: Unidade de lógica linear.
 */
public interface TaskInterceptor {
    TaskResult intercept(Chain chain);

    interface Chain {
        Map<String, JsonNode> inputs();
        TaskResult proceed(Map<String, JsonNode> inputs);
    }
}
