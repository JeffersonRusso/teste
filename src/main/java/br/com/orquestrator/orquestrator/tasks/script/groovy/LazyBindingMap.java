package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.core.context.WriteableContext;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class LazyBindingMap implements Map<String, Object> {

    private final ReadableContext reader;
    private final WriteableContext writer;

    @Override
    public Object get(Object key) {
        return reader.get((String) key);
    }

    @Override
    public Object put(String key, Object value) {
        // Corrigido: Usando DataValue.of
        writer.put(key, DataValue.of(value));
        return value;
    }

    @Override
    public int size() { return reader.getRoot().size(); }

    @Override
    public boolean isEmpty() { return reader.getRoot().isEmpty(); }

    @Override
    public boolean containsKey(Object key) { return reader.getRoot().containsKey(key); }

    @Override
    public boolean containsValue(Object value) { return reader.getRoot().containsValue(value); }

    @Override
    public Object remove(Object key) { return null; }

    @Override
    public void putAll(Map<? extends String, ?> m) { 
        // Corrigido: Usando referência de método com DataValue.of
        m.forEach((k, v) -> writer.put(k, DataValue.of(v))); 
    }

    @Override
    public void clear() {}

    @Override
    public Set<String> keySet() { return reader.getRoot().keySet(); }

    @Override
    public Collection<Object> values() { return reader.getRoot().values(); }

    @Override
    public Set<Entry<String, Object>> entrySet() { return reader.getRoot().entrySet(); }
}
