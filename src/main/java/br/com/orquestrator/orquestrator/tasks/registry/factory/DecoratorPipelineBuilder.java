package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.binding.MarshallingPlan;
import br.com.orquestrator.orquestrator.core.engine.runtime.*;
import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;

import java.util.ArrayList;
import java.util.List;

public class DecoratorPipelineBuilder {

    private final List<TaskDecorator> chain = new ArrayList<>();
    private final CompilationContext context;
    private final TaskDefinition def;
    private final String nodeId;

    public DecoratorPipelineBuilder(CompilationContext context, TaskDefinition def) {
        this.context = context;
        this.def = def;
        this.nodeId = def.nodeId().value();
    }

    public DecoratorPipelineBuilder withInfra() {
        chain.add(new ScopeDecorator(nodeId));
        chain.add(new TelemetryDecorator(nodeId));
        chain.add(new ErrorPolicyDecorator(nodeId, def.failFast()));
        return this;
    }

    public DecoratorPipelineBuilder withData(MarshallingPlan plan) {
        chain.add(new InputDecorator(context.marshaller(), plan));
        return this;
    }

    public DecoratorPipelineBuilder withConfigResolution(Class<?> configClass) {
        if (configClass != null) {
            chain.add(new ConfigurationResolverDecorator(context.bindingResolver(), def.config(), configClass));
        }
        return this;
    }

    public DecoratorPipelineBuilder withOutput(MarshallingPlan plan) {
        // Agora o OutputMappingDecorator recebe o validador e o registro de contratos
        chain.add(new OutputMappingDecorator(
            context.marshaller(), 
            context.dataValidator(), 
            context.contractRegistry(), 
            plan
        ));
        return this;
    }

    public DecoratorPipelineBuilder withFeatures() {
        List<FeatureDefinition> features = def.features();
        if (features != null && !features.isEmpty()) {
            chain.addAll(context.interceptorEngine().resolveInterceptors(features, nodeId));
        }
        return this;
    }

    public DecoratorPipelineBuilder withGuard() {
        if (def.guardCondition() != null && !def.guardCondition().isBlank()) {
            chain.add(new GuardDecorator(context.expressionEngine(), def.guardCondition(), nodeId));
        }
        return this;
    }

    public DecoratorPipelineBuilder withValidation() {
        chain.add(new ValidationDecorator(
            context.validator(), 
            context.dataValidator(), 
            context.contractRegistry(), 
            def
        ));
        return this;
    }

    public List<TaskDecorator> build() {
        return chain;
    }
}
