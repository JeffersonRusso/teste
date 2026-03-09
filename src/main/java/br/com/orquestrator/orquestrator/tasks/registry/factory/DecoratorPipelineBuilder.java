package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.runtime.*;
import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * DecoratorPipelineBuilder: Constrói a cadeia de interceptores para uma tarefa.
 */
public class DecoratorPipelineBuilder {

    private final List<TaskInterceptor> interceptors = new ArrayList<>();
    private final CompilationContext context;
    private final TaskDefinition def;
    private final String nodeId;

    public DecoratorPipelineBuilder(CompilationContext context, TaskDefinition def) {
        this.context = context;
        this.def = def;
        this.nodeId = def.nodeId().value();
    }

    public DecoratorPipelineBuilder withInfra() {
        interceptors.add(new TelemetryDecorator(nodeId));
        interceptors.add(new ErrorPolicyDecorator(nodeId, def.failFast()));
        return this;
    }

    public DecoratorPipelineBuilder withData() {
        interceptors.add(new InputDecorator(def.getRequiredFields()));
        return this;
    }

    public DecoratorPipelineBuilder withConfigResolution(Class<?> configClass, Object staticValue) {
        if (configClass != null) {
            interceptors.add(new ConfigurationResolverDecorator(context.bindingResolver(), def.config(), configClass));
        } else if (staticValue != null) {
            // Injeta a configuração estática já resolvida via um interceptor simples
            interceptors.add(chain -> chain.proceed(chain.inputs()));
        }
        return this;
    }

    public DecoratorPipelineBuilder withFeatures() {
        List<FeatureDefinition> features = def.features();
        if (features != null && !features.isEmpty()) {
            interceptors.addAll(context.interceptorEngine().resolveInterceptors(features, nodeId));
        }
        return this;
    }

    public DecoratorPipelineBuilder withValidation() {
        interceptors.add(new ValidationDecorator(
            context.validator(), 
            context.dataValidator(), 
            context.contractRegistry(), 
            def
        ));
        return this;
    }

    public List<TaskInterceptor> buildInterceptors() {
        return interceptors;
    }
}
