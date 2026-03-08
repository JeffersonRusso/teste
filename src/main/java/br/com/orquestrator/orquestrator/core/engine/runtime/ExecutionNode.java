package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.Map;

/**
 * ExecutionNode: Representa um nó participante do Signal Channel.
 * Suporta o padrão de Continuação para evitar trocas de contexto de thread (Context Switch).
 */
public interface ExecutionNode {
    
    /** Executa o nó e suas continuações. */
    void run(SignalRegistry signals);

    /** Coleta inputs dos sinais. */
    Map<String, DataValue> onSignal(SignalRegistry signals);

    /** Emite outputs nos sinais. */
    void emitSignal(SignalRegistry signals, DataValue resultBody);

    /** Adiciona um nó para ser executado na mesma thread (Continuação). */
    void then(ExecutionNode next);

    String nodeId();
}
