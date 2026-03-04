package br.com.orquestrator.orquestrator.core.context.validation;

import java.util.Map;

@FunctionalInterface
public interface Constraint {
    void validate(String key, Object value, Map<String, Object> currentData);
}
