package br.com.orquestrator.orquestrator.domain.rules;

/**
 * SemanticDefinition: Representa uma definição semântica no domínio.
 * Contém scripts para formatação e validação de dados.
 */
public record SemanticDefinition(
    String typeName,
    String description,
    String formatScript,
    String validationScript
) {
    public SemanticDefinition {
        if (typeName == null || typeName.isBlank()) {
            throw new IllegalArgumentException("O nome do tipo semântico não pode ser vazio");
        }
    }

    public boolean hasFormatScript() {
        return formatScript != null && !formatScript.isBlank();
    }

    public boolean hasValidationScript() {
        return validationScript != null && !validationScript.isBlank();
    }
}
