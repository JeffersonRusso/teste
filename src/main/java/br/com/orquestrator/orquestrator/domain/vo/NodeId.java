package br.com.orquestrator.orquestrator.domain.vo;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Value Object para identificar unicamente um nó (task) no pipeline.
 * Garante Type Safety e evita confusão com outros IDs.
 */
public record NodeId(@JsonValue String value) {
    
    public NodeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("NodeId não pode ser vazio");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
