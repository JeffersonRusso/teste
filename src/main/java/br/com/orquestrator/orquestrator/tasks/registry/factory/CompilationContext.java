package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.engine.runtime.InterceptorEngine;
import br.com.orquestrator.orquestrator.core.engine.validation.ContractValidator;
import br.com.orquestrator.orquestrator.core.engine.validation.TaskValidator;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * CompilationContext: Agrupa as ferramentas de infraestrutura necessárias para compilar pipelines e tasks.
 */
public record CompilationContext(
    DataMarshaller marshaller,
    TaskValidator validator,
    ContractValidator contractValidator,
    ExpressionEngine expressionEngine,
    InterceptorEngine interceptorEngine, // <--- NOVO
    ObjectMapper objectMapper
) {}
