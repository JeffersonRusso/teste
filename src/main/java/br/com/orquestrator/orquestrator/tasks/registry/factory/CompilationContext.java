package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.binding.InputCompiler;
import br.com.orquestrator.orquestrator.core.engine.binding.OutputCompiler;
import br.com.orquestrator.orquestrator.core.engine.binding.NormalizationCompiler;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.engine.runtime.InterceptorEngine;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractValidator;
import br.com.orquestrator.orquestrator.core.engine.validation.DataValidator;
import br.com.orquestrator.orquestrator.core.engine.validation.TaskValidator;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import com.fasterxml.jackson.databind.ObjectMapper;

public record CompilationContext(
    InputCompiler inputCompiler,
    OutputCompiler outputCompiler,
    NormalizationCompiler normalizationCompiler,
    TaskBindingResolver bindingResolver,
    TaskValidator validator,
    DataValidator dataValidator,
    ContractRegistry contractRegistry,
    ContractValidator contractValidator,
    ExpressionEngine expressionEngine,
    InterceptorEngine interceptorEngine,
    ObjectMapper objectMapper
) {}
