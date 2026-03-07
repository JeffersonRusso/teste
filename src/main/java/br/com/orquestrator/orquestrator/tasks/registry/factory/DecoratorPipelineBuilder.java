package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.core.engine.runtime.*;
import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;

import java.util.ArrayList;
import java.util.List;

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
        // OTIMIZAÇÃO: Removido ScopeDecorator para evitar overhead de ScopedValue.where()
        // O nodeId já está disponível no TaskContext.
        interceptors.add(new TelemetryDecorator(nodeId));
        interceptors.add(new ErrorPolicyDecorator(nodeId, def.failFast()));
        return this;
    }

    public DecoratorPipelineBuilder withData(MarshallingPlan plan) {
        interceptors.add(new InputDecorator(
            context.inputCompiler().bake(def),
            context.inputCompiler().extractRequiredFields(def)
        ));
        return this;
    }

    public DecoratorPipelineBuilder withConfigResolution(Class<?> configClass) {
        if (configClass != null) {
            interceptors.add(new ConfigurationResolverDecorator(context.bindingResolver(), def.config(), configClass));
        }
        return this;
    }

    public DecoratorPipelineBuilder withOutput(MarshallingPlan plan) {
        interceptors.add(new OutputMappingDecorator(
            context.outputCompiler().bake(def)
        ));
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
        if (def.guardCondition() != null && !def.guardCondition().isBlank()) {
            interceptors.add(new GuardDecorator(context.expressionEngine(), def.guardCondition(), nodeId));
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
