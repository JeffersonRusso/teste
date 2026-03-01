package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.stereotype.Component;

/**
 * ExecutionContextAccessor: Torna o ExecutionContext transparente para o SpEL.
 * Permite acessar 'raw', 'standard' e 'header' diretamente nas expressões.
 */
@Component
public class ExecutionContextAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{ExecutionContext.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return target instanceof ExecutionContext;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) {
        if (target instanceof ExecutionContext ctx) {
            // Busca direta no mapa de dados do contexto.
            // Se o SpEL pedir 'raw', o ctx.get('raw') retornará o mapa de dados brutos.
            Object value = ctx.get(name);
            return new TypedValue(value);
        }
        return TypedValue.NULL;
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) {
        return target instanceof ExecutionContext;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) {
        if (target instanceof ExecutionContext ctx) {
            ctx.put(name, newValue);
        }
    }
}
