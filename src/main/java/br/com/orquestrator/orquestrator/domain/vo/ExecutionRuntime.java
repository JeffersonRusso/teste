package br.com.orquestrator.orquestrator.domain.vo;

import java.time.Duration;
import java.time.Instant;

/**
 * Especialista no ciclo de vida temporal da requisição.
 * Java 21: Utiliza Records para imutabilidade e String Templates para erros.
 */
public record ExecutionRuntime(Instant deadline) {
    
    public ExecutionRuntime {
        if (deadline == null) {
            deadline = Instant.MAX;
        }
    }

    public long getRemainingTimeMs() {
        if (Instant.MAX.equals(deadline)) return Long.MAX_VALUE;
        try {
            return Math.max(0, Duration.between(Instant.now(), deadline).toMillis());
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * Verifica se ainda há tempo disponível para a execução de uma tarefa.
     * @param taskTimeout O timeout configurado para a tarefa.
     * @return O tempo efetivo que a tarefa pode rodar (mínimo entre o timeout e o deadline global).
     */
    public long checkTimeBudget(long taskTimeout) {
        long remaining = getRemainingTimeMs();
        if (remaining <= 0) {
            throw new IllegalStateException(STR."Time Budget esgotado em \{deadline}");
        }
        return Math.min(taskTimeout, remaining);
    }
}
