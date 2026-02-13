package br.com.orquestrator.orquestrator.tasks.common;

import br.com.orquestrator.orquestrator.domain.TaskMetadataHelper;
import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Especialista em mapear o resultado bruto de uma task para o TaskData.
 * Realiza o espalhamento de dados baseado no contrato (DataSpec) ou mapeamento total.
 * Java 21: Refatorado para maior clareza semântica e performance.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskResultMapper {

    private final ExpressionService expressionService;

    /**
     * Mapeia o resultado bruto para o barramento de dados da task.
     */
    public void mapResult(@NonNull final TaskData data, final Object result, @NonNull final TaskDefinition definition) {
        // 1. Garante metadados básicos de observabilidade
        data.addMetadata(TaskMetadataHelper.BODY, result);
        ensureDefaultStatus(data);

        final List<DataSpec> produces = definition.getProduces();

        // 2. Cenário: Sem contrato de saída (Mapeia tudo que puder se for um Mapa)
        if (produces == null || produces.isEmpty()) {
            mapEverything(data, result);
            return;
        }

        // 3. Cenário: Mapeamento Semântico (Usa o EvaluationContext para extrair caminhos específicos)
        mapSemanticOutputs(data, result, produces);
    }

    private void ensureDefaultStatus(TaskData data) {
        if (data.getMetadata(TaskMetadataHelper.STATUS) == null) {
            data.addMetadata(TaskMetadataHelper.STATUS, 200);
        }
    }

    private void mapEverything(TaskData data, Object result) {
        // Java 21: Pattern Matching para extração rápida de mapas
        if (result instanceof Map<?, ?> map) {
            map.forEach((k, v) -> data.put(String.valueOf(k), v));
        } else if (result != null) {
            log.trace("Resultado não é um mapa e não há 'produces' definido. O dado não foi espalhado.");
        }
    }

    private void mapSemanticOutputs(TaskData data, Object result, List<DataSpec> produces) {
        // Criamos o contexto de avaliação com o RESULTADO como raiz para navegação de caminhos
        final EvaluationContext evalContext = expressionService.create(result);

        // Java 21: Iteração funcional e limpa
        produces.forEach(spec -> {
            Object value = resolveValue(spec, result, evalContext);
            if (value != null) {
                data.put(spec.name(), value);
            }
        });
    }

    private Object resolveValue(DataSpec spec, Object root, EvaluationContext context) {
        // Se não tem path definido, o valor é o próprio objeto raiz (útil para retornos atômicos)
        if (!StringUtils.hasText(spec.path())) {
            return root;
        }

        // Tenta avaliar o path (ex: "user.id", "response.data[0]", etc)
        try {
            return context.evaluate(spec.path(), Object.class);
        } catch (Exception e) {
            log.debug("Não foi possível extrair o path '{}' do resultado da task.", spec.path());
            return null;
        }
    }
}
