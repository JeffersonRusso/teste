package br.com.orquestrator.orquestrator.infra.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * IdGenerator: Gerador de IDs ultra-rápido e lock-free para Virtual Threads.
 * Evita o bloqueio do SecureRandom/UUID.randomUUID().
 */
public final class IdGenerator {
    private static final AtomicLong COUNTER = new AtomicLong(System.currentTimeMillis());

    private IdGenerator() {}

    public static String nextId() {
        // Formato simples e rápido: timestamp + contador
        return Long.toHexString(COUNTER.getAndIncrement());
    }
}
