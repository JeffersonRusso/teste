package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Avaliador de Guardas (Condições de Execução).
 * Decide se uma task deve rodar baseada no estado atual dos dados.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GuardEvaluator {

    private final ExpressionService expressionService;

    public boolean shouldRun(String guardCondition, ExecutionContext context) {
        if (guardCondition == null || guardCondition.isBlank()) return true;

        try {
            // O context não é mais passado como parâmetro, o ExpressionService o pega do ScopedValue
            return expressionService.evaluate(guardCondition, Boolean.class);
        } catch (Exception e) {
            log.warn("Erro ao avaliar guarda '{}': {}. Assumindo FALSE (Segurança).", guardCondition, e.getMessage());
            return false;
        }
    }
}
