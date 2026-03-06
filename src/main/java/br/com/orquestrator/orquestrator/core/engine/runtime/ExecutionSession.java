package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.core.context.*;
import br.com.orquestrator.orquestrator.core.context.identity.RequestIdentity;
import br.com.orquestrator.orquestrator.core.context.tag.TagManager;
import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.engine.binding.ResultExtractor;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineService;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ExecutionSession: Orquestrador de fluxo Stateless e de alta performance.
 * Singleton: Instanciado uma única vez, sem overhead de ciclo de vida por request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionSession {

    private final ContextFactory contextFactory;
    private final TagManager tagManager;
    private final PipelineService pipelineService;
    private final ReactiveExecutionEngine engine;
    private final DataMarshaller marshaller;
    private final ResultExtractor resultExtractor;

    /**
     * Ponto de entrada único para a execução. 
     * O estado (contexto) vive apenas dentro do escopo deste método.
     */
    public Map<String, Object> run(RequestIdentity identity, Map<String, String> headers, Map<String, Object> body) {
        // Cria o contexto (objeto efêmero, mas leve, sem gerenciamento do Spring)
        ExecutionContext context = contextFactory.create(identity, headers, body);

        try {
            return ScopedValue.where(ContextHolder.CONTEXT, context).call(() -> {
                // 1. Identificação de Cenário
                tagManager.resolveAndApply(context.reader(), context.writer());

                // 2. Descoberta e Compilação (O cache do Registry garante a velocidade aqui)
                Pipeline pipeline = pipelineService.create(context.metadata());

                // 3. Normalização Inicial
                marshaller.executeNormalization(pipeline.normalizationPlan(), context.writer());

                // 4. Execução do Grafo (Virtual Threads)
                engine.execute(pipeline);

                // 5. Extração do Resultado
                return resultExtractor.extract(context.reader(), pipeline);
            });
        } catch (Exception e) {
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        }
    }
}
