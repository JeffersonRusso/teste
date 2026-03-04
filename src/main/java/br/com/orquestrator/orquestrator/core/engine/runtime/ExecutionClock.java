package br.com.orquestrator.orquestrator.core.engine.runtime;

import java.time.Duration;
import java.time.Instant;

/**
 * ExecutionClock: A única autoridade sobre prazos e timeouts.
 * Centraliza o cálculo de deadlines para evitar dispersão de lógica de tempo.
 */
public final class ExecutionClock {

    private ExecutionClock() {}

    /** Calcula o momento exato de expiração baseado em um timeout. */
    public static Instant calculateDeadline(Duration timeout) {
        return Instant.now().plus(timeout);
    }

    /** Calcula o momento exato de expiração baseado em milissegundos. */
    public static Instant calculateDeadline(long timeoutMs) {
        return Instant.now().plusMillis(timeoutMs);
    }

    /** Verifica se um prazo já foi atingido. */
    public static boolean isExpired(Instant deadline) {
        return Instant.now().isAfter(deadline);
    }

    /** Retorna o tempo restante até um prazo. */
    public static Duration remaining(Instant deadline) {
        Duration d = Duration.between(Instant.now(), deadline);
        return d.isNegative() ? Duration.ZERO : d;
    }
}
