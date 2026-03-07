package br.com.orquestrator.orquestrator.core.engine.binding;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.function.Function;

/**
 * NormalizationStep: Agora é puramente funcional.
 * Conhece apenas o contrato de transformação, não a tecnologia (SpEL).
 */
public record NormalizationStep(
    String target, 
    Function<ReadableContext, DataValue> transformation
) {}
