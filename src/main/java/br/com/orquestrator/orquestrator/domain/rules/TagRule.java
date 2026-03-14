package br.com.orquestrator.orquestrator.domain.rules;

/**
 * TagRule: Representa uma regra dinâmica para ativação de tags no domínio.
 * 
 * Regras com maior prioridade devem ser avaliadas primeiro.
 */
public record TagRule(
    String tagName,
    String conditionExpression,
    int priority,
    boolean active
) {
    /**
     * Construtor defensivo para garantir integridade.
     */
    public TagRule {
        if (tagName == null || tagName.isBlank()) {
            throw new IllegalArgumentException("Nome da tag não pode ser vazio");
        }
        if (conditionExpression == null || conditionExpression.isBlank()) {
            throw new IllegalArgumentException("Expressão de condição não pode ser vazia");
        }
    }

    /**
     * Compara prioridades para ordenação descendente.
     */
    public int compareTo(TagRule other) {
        return Integer.compare(other.priority(), this.priority());
    }
}
