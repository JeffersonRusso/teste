package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[]{ReadableContext.class, DataValue.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        return target instanceof ReadableContext || target instanceof DataValue;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) {
        if (target instanceof ReadableContext rc) {
            return unwrapIfScalar(rc.get(name));
        }

        if (target instanceof DataValue dv) {
            return switch (dv) {
                case DataValue.Mapping(var fields, var type) -> {
                    // PROMOÇÃO LAZY: Converte o valor bruto do mapa para DataValue
                    Object rawValue = fields.get(name);
                    yield unwrapIfScalar(DataValue.of(rawValue));
                }
                case DataValue.DomainObject(var obj, var type) -> readReflectively(obj, name);
                default -> TypedValue.NULL;
            };
        }

        return TypedValue.NULL;
    }

    private TypedValue unwrapIfScalar(DataValue dv) {
        if (dv == null || dv instanceof DataValue.Empty) return TypedValue.NULL;
        
        return switch (dv) {
            case DataValue.Numeric(var n, var type) -> new TypedValue(n);
            case DataValue.Text(var s, var type) -> new TypedValue(s);
            case DataValue.Logic(var b, var type) -> new TypedValue(b);
            default -> new TypedValue(dv);
        };
    }

    private TypedValue readReflectively(Object obj, String name) {
        return null;
    }

    @Override public boolean canWrite(EvaluationContext context, Object target, String name) { return false; }
    @Override public void write(EvaluationContext context, Object target, String name, Object newValue) {}
}
