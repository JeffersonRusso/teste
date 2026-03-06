package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import org.springframework.expression.Expression;

/**
 * ExpressionEngine: Fachada minimalista para o motor de expressões.
 */
public interface ExpressionEngine {
    
    /** Avalia um valor (String, Template ou Literal) contra um objeto raiz. */
    DataValue evaluate(Object value, Object root);

    /** Avalia e converte para um tipo Java específico. */
    <T> T evaluate(Object value, Object root, Class<T> targetType);

    /** Grava um valor em um caminho específico. */
    void setValue(Object root, String path, Object value);

    /** Pré-compila uma expressão para uso posterior. */
    Expression parse(String expression);

    /** Executa uma expressão pré-compilada. */
    DataValue execute(Expression expression, Object root);
}
