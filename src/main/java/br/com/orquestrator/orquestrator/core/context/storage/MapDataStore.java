package br.com.orquestrator.orquestrator.core.context.storage;

import br.com.orquestrator.orquestrator.core.context.ReadableContext;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MapDataStore: Armazenamento focado em DataValue.
 * Mantém a integridade do domínio e evita o uso de Object genérico.
 */
public class MapDataStore implements DataStore, ReadableContext {

    private final Map<String, DataValue> storage = new ConcurrentHashMap<>(64);

    @Override
    public void put(String key, DataValue value) {
        DataPath path = DataPath.of(key);
        
        if (path.getParentPath().isEmpty()) {
            storage.put(path.getLeafName(), value);
            return;
        }

        DataValue parentNode = ensurePath(path.getParentPath());
        if (parentNode instanceof DataValue.Mapping m) {
            // Cast seguro: Mapping sempre contém Map<String, DataValue> no nosso design
            ((Map<String, Object>) m.fields()).put(path.getLeafName(), value);
        }
    }

    @Override
    public DataValue get(String key) {
        DataPath path = DataPath.of(key);
        DataValue current = null;

        for (String part : path.getParts()) {
            if (current == null) {
                current = storage.get(part);
            } else if (current instanceof DataValue.Mapping m) {
                Object val = m.fields().get(part);
                current = (val instanceof DataValue dv) ? dv : DataValue.of(val);
            } else {
                return new DataValue.Empty();
            }
            if (current == null) return new DataValue.Empty();
        }
        return current != null ? current : new DataValue.Empty();
    }

    @Override
    public Object getRaw(String key) {
        return get(key).raw();
    }

    private DataValue ensurePath(String path) {
        DataPath dataPath = DataPath.of(path);
        Map<String, DataValue> currentMap = storage;
        DataValue lastNode = null;

        for (String part : dataPath.getParts()) {
            lastNode = currentMap.computeIfAbsent(part, k -> 
                new DataValue.Mapping(new ConcurrentHashMap<>(), null));
            
            if (lastNode instanceof DataValue.Mapping m) {
                currentMap = (Map<String, DataValue>) m.fields();
            } else {
                Map<String, DataValue> newMap = new ConcurrentHashMap<>();
                lastNode = new DataValue.Mapping(newMap, null);
                currentMap.put(part, lastNode);
                currentMap = newMap;
            }
        }
        return lastNode;
    }

    @Override public Map<String, Object> getRoot() {
        Map<String, Object> rawMap = new java.util.HashMap<>();
        storage.forEach((k, v) -> rawMap.put(k, v.raw()));
        return rawMap;
    }

    @Override public Map<String, Object> getAll() { return getRoot(); }
    @Override public boolean contains(String key) { return !(get(key) instanceof DataValue.Empty); }
}
