package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * InputDecorator: Garante a integridade dos inputs para a tarefa.
 */
@RequiredArgsConstructor
public class InputDecorator implements TaskInterceptor {

    private final Set<String> requiredFields;

    @Override
    public TaskResult intercept(Chain chain) {
        // No novo modelo, apenas repassamos os inputs. 
        // A validação de campos obrigatórios agora é feita pelo ValidationDecorator.
        return chain.proceed(chain.inputs());
    }
}
