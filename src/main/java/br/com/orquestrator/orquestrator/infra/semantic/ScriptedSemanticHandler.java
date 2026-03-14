package br.com.orquestrator.orquestrator.infra.semantic;

import br.com.orquestrator.orquestrator.domain.model.SemanticHandler;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * ScriptedSemanticHandler: Implementação do handler semântico que utiliza scripts dinâmicos.
 * Totalmente desacoplado do motor de script real, delegando para o ExpressionEngine.
 */
@RequiredArgsConstructor
public class ScriptedSemanticHandler implements SemanticHandler {

    private final String typeName;
    private final String formatScript;
    private final String validationScript;
    private final ExpressionEngine expressionEngine;

    @Override
    public Object format(Object value) {
        if (formatScript == null || formatScript.isBlank()) return value;
        try {
            return expressionEngine.compile(formatScript)
                    .evaluate(Map.of("value", value), Object.class);
        } catch (Exception e) {
            return value;
        }
    }

    @Override
    public boolean isValid(Object value) {
        if (validationScript == null || validationScript.isBlank()) return true;
        try {
            return Boolean.TRUE.equals(
                expressionEngine.compile(validationScript)
                        .evaluate(Map.of("value", value), Boolean.class)
            );
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getTypeName() {
        return typeName;
    }
}
