package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

/**
 * DataNodePropertyAccessor: Garante que o SpEL receba valores nativos em nós folha.
 */
public class DataNodePropertyAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[] { DataNode.class };
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
        return target instanceof DataNode;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        DataNode dn = (DataNode) target;
        DataNode result = dn.get(name);
        
        if (result.isMissing()) return TypedValue.NULL;

        // CORREÇÃO: Se for um valor folha, entrega o valor nativo IMEDIATAMENTE.
        // Isso impede que o SpEL tente usar o DataNode como uma String em templates.
        if (result.isValue()) {
            return new TypedValue(result.asNative());
        }

        // Se for objeto/array, mantém como DataNode para navegação profunda.
        return new TypedValue(result);
    }

    @Override public boolean canWrite(EvaluationContext context, Object target, String name) { return false; }
    @Override public void write(EvaluationContext context, Object target, String name, Object newValue) {}
}
