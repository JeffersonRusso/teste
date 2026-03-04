package br.com.orquestrator.orquestrator.core.context;

import java.util.Map;

public interface ReadableContext {
    Object get(String key);
    Map<String, Object> getRoot();
}
