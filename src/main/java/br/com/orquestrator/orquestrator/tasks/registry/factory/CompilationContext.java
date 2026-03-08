package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.core.engine.runtime.InterceptorEngine;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractRegistry;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractValidator;
import br.com.orquestrator.orquestrator.core.engine.validation.DataValidator;
import br.com.orquestrator.orquestrator.core.engine.validation.TaskValidator;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * CompilationContext: Agrupa as dependências necessárias para a compilação de tarefas.
 * Agora 100% limpo de compiladores de binding obsoletos.
 */
public record CompilationContext(
    TaskBindingResolver bindingResolver,
    TaskValidator validator,
    DataValidator dataValidator,
    ContractRegistry contractRegistry,
    ContractValidator contractValidator,
    ExpressionEngine expressionEngine,
    InterceptorEngine interceptorEngine,
    ObjectMapper objectMapper
) {}
