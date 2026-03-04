package br.com.orquestrator.orquestrator.core.context;

public interface WriteableContext {
    void put(String key, Object value);
    void addTag(String tag);
}
