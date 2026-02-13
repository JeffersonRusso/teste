package br.com.orquestrator.orquestrator.core.engine;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Path;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Barramento de dados resiliente e responsivo a interrupções.
 */
@Slf4j
public class DataBus {

    private final Map<String, CompletableFuture<Object>> bus = new ConcurrentHashMap<>();

    public DataBus(ExecutionContext context, List<TaskDefinition> tasksToRun) {
        tasksToRun.stream()
                .map(TaskDefinition::getProduces)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(spec -> bus.putIfAbsent(spec.name(), new CompletableFuture<>()));

        context.getDataStore().forEach((key, value) -> 
            bus.computeIfAbsent(key, k -> CompletableFuture.completedFuture(value))
               .complete(value)
        );
    }

    /**
     * Aguarda as dependências de forma interruptível.
     * Essencial para funcionar corretamente com StructuredTaskScope e Virtual Threads.
     */
    public void waitForDependencies(TaskDefinition taskDef) throws InterruptedException, ExecutionException {
        List<DataSpec> requires = taskDef.getRequires();
        if (requires == null || requires.isEmpty()) return;

        CompletableFuture<?>[] dependencies = requires.stream()
                .map(spec -> resolveToken(spec.name()))
                .filter(Objects::nonNull)
                .toArray(CompletableFuture[]::new);

        if (dependencies.length > 0) {
            // Usamos get() em vez de join() para respeitar a interrupção da thread
            try {
                CompletableFuture.allOf(dependencies).get();
            } catch (ExecutionException e) {
                // Se uma dependência falhou, apenas propagamos. 
                // O TaskRunner cuidará de logar o erro da task atual.
                log.trace("Dependência falhou para task {}", taskDef.getNodeId());
                throw e;
            }
        }
    }

    private CompletableFuture<Object> resolveToken(String key) {
        CompletableFuture<Object> future = bus.get(key);
        if (future != null) return future;

        Path path = Path.of(key);
        return path.isNested() ? bus.get(path.root()) : null;
    }

    public void publishResults(TaskDefinition taskDef, ExecutionContext context) {
        completeFutures(taskDef.getProduces(), context::get);
    }

    public void failResults(TaskDefinition taskDef) {
        completeFutures(taskDef.getProduces(), _ -> null);
    }

    private void completeFutures(List<DataSpec> outputs, Function<String, Object> valueProvider) {
        if (outputs == null) return;
        outputs.forEach(spec -> {
            CompletableFuture<Object> future = bus.get(spec.name());
            if (future != null && !future.isDone()) {
                future.complete(valueProvider.apply(spec.name()));
            }
        });
    }
}
