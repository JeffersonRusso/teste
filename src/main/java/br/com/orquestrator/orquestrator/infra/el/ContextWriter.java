package br.com.orquestrator.orquestrator.infra.el;

import lombok.RequiredArgsConstructor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ContextWriter: Especialista em gravar dados em estruturas complexas usando SpEL.
 * Utiliza a habilidade de Auto-Grow do Spring para criar caminhos inexistentes.
 */
@Component
@RequiredArgsConstructor
public class ContextWriter {

    private final SpelExpressionParser parser;
    private final Map<String, Expression> writeCache = new ConcurrentHashMap<>(512);

    public void write(Map<String, Object> root, String path, Object value) {
        if (path == null || path.isBlank()) return;

        // Se for chave simples, grava direto no mapa para performance máxima
        if (path.indexOf('.') == -1) {
            root.put(path, value);
            return;
        }

        // Se for caminho complexo, usa SpEL com Auto-Grow
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        Expression exp = writeCache.computeIfAbsent(path, parser::parseExpression);
        exp.setValue(context, value);
    }
}
