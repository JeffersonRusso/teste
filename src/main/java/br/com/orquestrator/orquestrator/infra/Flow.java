package br.com.orquestrator.orquestrator.infra;

import java.util.function.Function;

/**
 * Flow: Implementação do padrão Pipeline/Monad para encadeamento de lógica.
 * Permite que o código pareça um DAG de transformações.
 */
public final class Flow<T> {
    private final T value;

    private Flow(T value) {
        this.value = value;
    }

    public static <T> Flow<T> start(T value) {
        return new Flow<>(value);
    }

    public <R> Flow<R> next(Function<T, R> mapper) {
        return new Flow<>(mapper.apply(value));
    }

    public T get() {
        return value;
    }

    public <R> R finish(Function<T, R> finalizer) {
        return finalizer.apply(value);
    }
}
