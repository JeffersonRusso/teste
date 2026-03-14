package br.com.orquestrator.orquestrator.api.signal;

import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import java.util.Optional;

/**
 * SignalResult: Representa o estado de um sinal resolvido.
 */
public sealed interface SignalResult {
    
    record Present(DataNode value) implements SignalResult {}
    record Empty() implements SignalResult {}
    record Failed(Throwable cause) implements SignalResult {}
    record Pending() implements SignalResult {}

    /**
     * Lei de Deméter: Retorna o valor nativo se presente, ou vazio.
     */
    default Optional<Object> getValueAsNative() {
        if (this instanceof Present p) {
            return Optional.ofNullable(p.value().asNative());
        }
        return Optional.empty();
    }

    /**
     * Atalho para verificar sucesso.
     */
    default boolean isPresent() {
        return this instanceof Present;
    }
}
