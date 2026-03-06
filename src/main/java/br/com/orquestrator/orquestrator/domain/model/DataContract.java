package br.com.orquestrator.orquestrator.domain.model;

/**
 * DataContract: O RG de um dado no banco de contexto.
 */
public record DataContract(
    String contextKey,
    DataType type,
    String semanticType, // <--- NOVO: Referência para tb_semantic_definition
    String formatRule,
    String schemaDefinition,
    Double minValue,
    Double maxValue,
    boolean required,
    String description
) {}
