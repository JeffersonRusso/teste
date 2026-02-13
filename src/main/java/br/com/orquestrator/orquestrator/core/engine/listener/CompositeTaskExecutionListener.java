package br.com.orquestrator.orquestrator.core.engine.listener;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * Implementação Composite que encapsula múltiplos listeners.
 * Garante que a falha de um listener não interrompa o fluxo principal ou outros listeners.
 * Utiliza recursos do Java 21 para garantir uma iteração limpa e segura.
 */
@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class CompositeTaskExecutionListener implements TaskExecutionListener {

    private final List<TaskExecutionListener> listeners;

    @Override
    public void onStart(TaskDefinition taskDef, ExecutionContext context) {
        notifyAll(l -> l.onStart(taskDef, context), "onStart");
    }

    @Override
    public void onSuccess(TaskDefinition taskDef, ExecutionContext context) {
        notifyAll(l -> l.onSuccess(taskDef, context), "onSuccess");
    }

    @Override
    public void onError(TaskDefinition taskDef, ExecutionContext context, Exception e) {
        notifyAll(l -> l.onError(taskDef, context, e), "onError");
    }

    /**
     * Método auxiliar para iterar sobre a SequencedCollection (List) de forma segura.
     * O uso de Consumer permite centralizar o tratamento de erros e o isolamento.
     */
    private void notifyAll(Consumer<TaskExecutionListener> action, String stage) {
        listeners.stream()
                .filter(l -> l != this) // Evita recursão infinita caso o composite seja injetado na lista
                .forEach(l -> {
                    try {
                        action.accept(l);
                    } catch (Exception ex) {
                        log.error("Erro no listener [{}] durante o estágio [{}]: {}", 
                                  l.getClass().getSimpleName(), stage, ex.getMessage());
                    }
                });
    }
}
