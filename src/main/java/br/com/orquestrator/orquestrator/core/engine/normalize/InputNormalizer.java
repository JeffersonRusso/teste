package br.com.orquestrator.orquestrator.core.engine.normalize;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.el.SpelContextFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class InputNormalizer {

    private final SpelContextFactory contextFactory;

    public void normalize(ExecutionContext context, Pipeline pipeline) {
        var mapping = pipeline.inputMapping();
        if (mapping == null || mapping.isEmpty()) return;

        Object rawData = context.get("raw");
        // Usa o factory para criar o contexto de avaliação inicial
        var evalContext = contextFactory.create(context, Map.of("raw", rawData != null ? rawData : Map.of()));

        mapping.forEach((targetKey, sourceExpression) -> {
            try {
                Object value = evalContext.evaluate(sourceExpression, Object.class);
                if (value != null) {
                    context.put("standard." + targetKey, value);
                }
            } catch (Exception e) {
                throw new PipelineException("Falha na normalização do campo '" + targetKey + "'", e);
            }
        });
    }
}
