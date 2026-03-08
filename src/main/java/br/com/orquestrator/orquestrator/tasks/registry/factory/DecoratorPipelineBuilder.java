package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.core.engine.runtime.*;
import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
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

    public DecoratorPipelineBuilder withData(MarshallingPlan plan) {
        interceptors.add(new InputDecorator(def.getRequiredFields()));
        return this;
    }

    /**
     * Resolve a configuração: se for dinâmica, adiciona o decorator. 
     * Se for estática, injeta o valor pronto no contexto.
     */
    public DecoratorPipelineBuilder withConfigResolution(Class<?> configClass, Object staticValue) {
        if (configClass != null) {
            interceptors.add(new ConfigurationResolverDecorator(context.bindingResolver(), def.config(), configClass));
        } else if (staticValue != null) {
            // Injeta a configuração estática já resolvida
            interceptors.add(chain -> chain.proceed(chain.context().withConfiguration(DataValueFactory.of(staticValue))));
        }
        return this;
    }

    public DecoratorPipelineBuilder withOutput(MarshallingPlan plan) {
        return this;
    }

    public DecoratorPipelineBuilder withFeatures() {
        List<FeatureDefinition> features = def.features();
        if (features != null && !features.isEmpty()) {
            interceptors.addAll(context.interceptorEngine().resolveInterceptors(features, nodeId));
        }
        return this;
    }

    public DecoratorPipelineBuilder withGuard() {
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
