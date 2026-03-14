package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import java.util.Map;

/**
 * DataNodeAwareMapAccessor: Intercepta acessos a mapas e desembrulha DataNodes.
 */
public class DataNodeAwareMapAccessor extends MapAccessor {

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
        return target instanceof Map;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        TypedValue tv = super.read(context, target, name);
        Object value = tv.getValue();

        if (value instanceof DataNode dn) {
            if (dn.isMissing()) return TypedValue.NULL;
            
            // Se for valor final, entrega a "carne".
            if (dn.isValue()) {
                return new TypedValue(dn.asNative());
            }
            // Se for objeto, entrega o nó para o DataNodePropertyAccessor navegar em raw.id
            return new TypedValue(dn);
        }

        return tv;
    }
}
