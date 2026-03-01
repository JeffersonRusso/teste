package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.StructuredTaskScope;

/**
 * ReactiveExecutionEngine: Otimizado para 1k TPS.
 * Usa CompletableFuture como sinalizadores leves e StructuredTaskScope para controle.
 */
@Component
@RequiredArgsConstructor
public class ReactiveExecutionEngine {

    private final TaskRunner taskRunner;

    public void execute(Pipeline pipeline) {
        // 1. Mapa de Sinais: Pré-dimensionado para evitar redimensionamento (Hot Path)
        // 50 tasks * 2 outputs = 100. 128 é a potência de 2 ideal.
        var signals = new ConcurrentHashMap<String, CompletableFuture<Void>>(128);

        // 2. Registro de intenção (Zero alocação de lógica, apenas placeholders)
        for (var node : pipeline.getNodes()) {
            for (var output : node.outputs()) {
                signals.put(output.targetKey(), new CompletableFuture<>());
            }
        }

        // 3. Execução Concorrente Estruturada
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            for (var node : pipeline.getNodes()) {
                scope.fork(() -> {
                    try {
                        // ESPERA: Bloqueio ultra-rápido em Virtual Thread
                        for (var input : node.inputs()) {
                            var s = signals.get(input.contextKey());
                            if (s != null) s.join();
                        }
                        taskRunner.run(node);
                    } finally {
                        // SINALIZA: Libera dependentes
                        for (var output : node.outputs()) {
                            signals.get(output.targetKey()).complete(null);
                        }
                    }
                    return null;
                });
            }
            // Aguarda o pipeline inteiro respeitando o timeout global
            scope.joinUntil(Instant.now().plus(pipeline.timeout())).throwIfFailed();
        } catch (Exception e) {
            throw (e instanceof PipelineException pe) ? pe : new PipelineException("Falha no pipeline", e);
        }
    }
}
