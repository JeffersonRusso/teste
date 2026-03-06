package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskContext;
import br.com.orquestrator.orquestrator.tasks.base.TaskResult;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ConfigurationResolverDecorator implements TaskDecorator {

    private final TaskBindingResolver bindingResolver;
    private final Map<String, Object> rawConfig;
    private final Class<?> configClass;

    @Override
    public TaskResult apply(TaskContext context, TaskChain next) {
        // Resolve a configuração usando os inputs do contexto (que já foram populados pelo InputDecorator)
        // O TaskBindingResolver foi atualizado para usar CURRENT_INPUTS, mas aqui podemos passar explicitamente
        // se refatorarmos o TaskBindingResolver para aceitar inputs.
        // Por enquanto, confiamos que o InputDecorator já setou o ScopedValue.
        
        Object resolvedConfig = bindingResolver.resolve(rawConfig, configClass);
        
        // Cria um novo contexto enriquecido com a configuração
        TaskContext enrichedContext = new TaskContext(context.inputs(), DataValue.of(resolvedConfig), context.nodeId());
        
        return next.proceed(enrichedContext);
    }
}
