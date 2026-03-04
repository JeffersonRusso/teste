package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import java.util.Map;

/**
 * TaskInputs: Ponto de acesso aos dados locais da task atual.
 * Isola a task da estrutura global do contexto.
 */
public final class TaskInputs {

    private TaskInputs() {}

    public static Object get(String localKey) {
        Map<String, Object> inputs = ContextHolder.CURRENT_INPUTS.get();
        return inputs != null ? inputs.get(localKey) : null;
    }

    public static <T> T get(String localKey, Class<T> type) {
        Object value = get(localKey);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public static String getString(String localKey) {
        return get(localKey, String.class);
    }
}
