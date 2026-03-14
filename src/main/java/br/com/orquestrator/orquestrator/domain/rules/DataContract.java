package br.com.orquestrator.orquestrator.domain.rules;

/**
 * DataContract: Representa um contrato de dados no domínio.
 * 
 * Este objeto carrega a definição do schema que será validado pelo motor.
 */
public record DataContract(
    String key,
    String schemaDefinition,
    String description
) {
    public DataContract {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Chave do contrato não pode ser vazia");
        }
    }

    public boolean hasSchema() {
        return schemaDefinition != null && !schemaDefinition.isBlank();
    }
}
