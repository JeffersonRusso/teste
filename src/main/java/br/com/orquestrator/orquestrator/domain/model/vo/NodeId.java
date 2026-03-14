package br.com.orquestrator.orquestrator.domain.model.vo;

/**
 * NodeId: Identidade única de um nó no grafo.
 */
public record NodeId(String value) {
    public static NodeId of(String value) { return new NodeId(value); }
    @Override public String toString() { return value; }
}
