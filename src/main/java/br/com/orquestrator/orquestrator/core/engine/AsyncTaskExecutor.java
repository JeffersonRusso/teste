package br.com.orquestrator.orquestrator.core.engine;

import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;

/**
 * AsyncTaskExecutor: Especialista em Concorrência Estruturada.
 * Sem travas artificiais, permitindo que o Java 21 gerencie as Virtual Threads.
 */
@Component
public class AsyncTaskExecutor {

    public void executeParallel(List<Runnable> tasks, Duration timeout) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            tasks.forEach(task -> scope.fork(() -> {
                task.run();
                return null;
            }));
            scope.joinUntil(java.time.Instant.now().plus(timeout)).throwIfFailed();
        } catch (Exception e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
