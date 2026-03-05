package br.com.orquestrator.orquestrator.domain.model;

/**
 * DataContract: O RG de um dado no banco de contexto.
 * Suporta tipos simples e objetos complexos via JSON Schema.
 */
public record DataContract(
    String contextKey,
    DataType type,
    String formatRule,
    String schemaDefinition, // NOVO: Definição do objeto complexo
    Double minValue,
    Double maxValue,
    boolean required,
    String description
) {}
