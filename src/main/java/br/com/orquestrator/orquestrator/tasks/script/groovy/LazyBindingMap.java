package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import java.util.AbstractMap;
import java.util.Set;

/**
 * Uma "View" viva e de zero-cópia do ExecutionContext para scripts Groovy.
 * Permite que o script navegue e modifique o contexto como se fosse um Map nativo.
 */
public class LazyBindingMap extends AbstractMap<String, Object> {
    private final ExecutionContext context;

    public LazyBindingMap(ExecutionContext context) {
        this.context = context;
    }

    @Override
    public Object get(Object key) {
        if (key == null) return null;
        return context.get(key.toString());
    }

    @Override
    public Object put(String key, Object value) {
        context.put(key, value);
        return value;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return context.getRoot().entrySet();
    }
}
