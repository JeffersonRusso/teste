package br.com.orquestrator.orquestrator.infra.semantic;

import br.com.orquestrator.orquestrator.domain.model.SemanticHandler;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import lombok.Getter;

import java.util.Map;

public class ScriptedSemanticHandler implements SemanticHandler {

    @Getter
    private final String typeName;
    private final Expression formatExpr;
    private final Expression plusExpr;
    private final Expression concatExpr;

    public ScriptedSemanticHandler(String typeName, String formatScript, String plusScript, String concatScript) {
        this.typeName = typeName;
        this.formatExpr = formatScript != null && !formatScript.isBlank() ? AviatorEvaluator.compile(formatScript) : null;
        this.plusExpr = plusScript != null && !plusScript.isBlank() ? AviatorEvaluator.compile(plusScript) : null;
        this.concatExpr = concatScript != null && !concatScript.isBlank() ? AviatorEvaluator.compile(concatScript) : null;
    }

    @Override
    public String format(Object value) {
        if (formatExpr == null) return value != null ? value.toString() : "";
        return formatExpr.execute(Map.of("it", value)).toString();
    }

    @Override
    public Object plus(Object value, Object other) {
        if (plusExpr == null) return value.toString() + other.toString(); // Fallback
        return plusExpr.execute(Map.of("it", value, "other", other));
    }

    @Override
    public Object concat(Object value, Object other) {
        if (concatExpr == null) return value.toString() + other.toString();
        return concatExpr.execute(Map.of("it", value, "other", other));
    }
}
