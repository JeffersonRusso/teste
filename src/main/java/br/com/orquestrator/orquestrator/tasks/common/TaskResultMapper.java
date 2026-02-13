package br.com.orquestrator.orquestrator.tasks.common;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskResultMapper {

    private final ExpressionService expressionService;
    private final ConversionService conversionService;
    private final ObjectMapper objectMapper;

    public void mapResult(@NonNull final TaskData data, final Object result, @NonNull final TaskDefinition definition) {
        data.addMetadata(TaskMetadataHelper.BODY, result);
        
        if (isNull(data.getMetadata(TaskMetadataHelper.STATUS))) {
            data.addMetadata(TaskMetadataHelper.STATUS, 200);
        }

        final List<DataSpec> produces = definition.getProduces();
        if (isNull(produces) || produces.isEmpty()) {
            if (nonNull(result)) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> map = conversionService.convert(result, Map.class);
                if (nonNull(map)) {
                    map.forEach(data::put);
                }
            }
            return;
        }

        mapSemanticOutputs(data, result, produces);
    }

    private void mapSemanticOutputs(final TaskData data, final Object result, final List<DataSpec> produces) {
        final EvaluationContext evalContext = expressionService.create(result);

        for (int i = 0; i < produces.size(); i++) {
            DataSpec spec = produces.get(i);
            final Object value = resolveValue(spec, result, evalContext);
            
            if (nonNull(value)) {
                data.put(spec.name(), value);
            }
        }
    }

    private Object resolveValue(final DataSpec spec, final Object rootResult, final EvaluationContext evalContext) {
        if (StringUtils.hasText(spec.path())) {
            if (rootResult instanceof Map<?, ?> mapResult && mapResult.containsKey(spec.path())) {
                return mapResult.get(spec.path());
            }
            try {
                return evalContext.evaluate(spec.path(), Object.class);
            } catch (Exception e) {
                log.trace("Falha ao avaliar path '{}': {}", spec.path(), e.getMessage());
            }
        }
        
        if (!StringUtils.hasText(spec.path())) {
            return rootResult;
        }

        return null;
    }
}
