package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import java.util.Map;

/**
 * SignalProjector: Interface para a lógica de projeção de sinais.
 * Isola como os dados são mapeados entre o SignalRegistry e as Tasks.
 */
public interface SignalProjector {
    
    /** Transforma sinais do Registry em inputs para a Task. */
    Map<String, DataValue> projectIn(SignalRegistry signals);

    /** Transforma o resultado da Task em sinais no Registry. */
    void projectOut(DataValue result, SignalRegistry signals);

    /** Propaga falha para os sinais que este projetor deveria emitir. */
    void fail(SignalRegistry signals, Throwable cause);
}
