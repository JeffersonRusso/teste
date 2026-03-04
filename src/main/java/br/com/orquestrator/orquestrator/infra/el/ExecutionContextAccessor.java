package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.stereotype.Component;

/**
 * ExecutionContextAccessor: O único tradutor entre SpEL e o Banco de Contexto.
 * Permite acesso direto a chaves (ex: raw.user.id) sem necessidade de prefixos mágicos.
 */
@Component
public class ExecutionContextAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{ReadableContext.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return target instanceof ReadableContext;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) {
        if (target instanceof ReadableContext rc) {
            // Delega a navegação para o MapDataStore, que já sabe lidar com pontos
            return new TypedValue(rc.get(name));
        }
        return TypedValue.NULL;
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) {
        return false;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) {
        // No-op: Escrita é feita via WriteableContext.put()
    }
}
