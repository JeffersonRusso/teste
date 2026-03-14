package br.com.orquestrator.orquestrator.core.engine.support;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * ExecutionClock: A autoridade sobre o tempo.
 * Permite injeção de Clock para testes determinísticos.
 */
public final class ExecutionClock {

    private static Clock clock = Clock.systemUTC();

    private ExecutionClock() {}

    /** Permite alterar o relógio global (Útil APENAS para testes). */
    public static void setClock(Clock newClock) {
        clock = newClock;
    }

    public static void reset() {
        clock = Clock.systemUTC();
    }

    public static Instant now() {
        return Instant.now(clock);
    }

    public static Instant calculateDeadline(Duration timeout) {
        return now().plus(timeout);
    }

    public static Instant calculateDeadline(long timeoutMs) {
        return now().plusMillis(timeoutMs);
    }

    public static boolean isExpired(Instant deadline) {
        return now().isAfter(deadline);
    }

    public static Duration remaining(Instant deadline) {
        Duration d = Duration.between(now(), deadline);
        return d.isNegative() ? Duration.ZERO : d;
    }
}
