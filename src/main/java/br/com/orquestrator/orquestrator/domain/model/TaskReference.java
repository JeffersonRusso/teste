package br.com.orquestrator.orquestrator.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskReference(
    String id,
    Integer version
) {
    public TaskReference {
        if (version == null) version = 1; // Default para v1 se não especificado
    }
}
