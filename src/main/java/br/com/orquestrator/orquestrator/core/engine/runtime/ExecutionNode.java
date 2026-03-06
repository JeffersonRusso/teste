package br.com.orquestrator.orquestrator.core.engine.runtime;

/**
 * ExecutionNode: Representa um nó participante do Signal Channel.
 * Encapsula a lógica de dependência e propagação de sinais.
 */
public interface ExecutionNode {
    
    /**
     * Executa o ciclo de vida do nó: onSignal -> task -> emitSignal.
     */
    void run(SignalRegistry signals);

    /**
     * Aguarda os sinais de entrada (dependências) necessários para este nó.
     */
    void onSignal(SignalRegistry signals);

    /**
     * Emite os sinais de saída (conclusão) produzidos por este nó.
     */
    void emitSignal(SignalRegistry signals);

    String nodeId();
}
