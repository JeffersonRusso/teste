package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * PipelineService: Ponto de entrada para obtenção de pipelines executáveis.
 * Gerencia o ciclo de vida: Carga -> Compilação -> Cache.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineCompiler pipelineCompiler;

    /**
     * Obtém um pipeline pronto para execução.
     * Otimizado com cache baseado no tipo de operação e cenário (tags).
     */
    @Cacheable(value = "compiled_pipelines", key = "#context.operationType + ':' + #context.tags")
    public Pipeline create(ExecutionContext context) {
        log.info("Solicitando pipeline para: {} | Tags: {}", context.getOperationType(), context.getTags());

        // 1. Carrega a definição (Snapshot do Banco)
        PipelineDefinition definition = loadDefinition(context.getOperationType());

        // 2. Compila e Otimiza para o cenário atual
        return pipelineCompiler.compile(definition, context.getTags());
    }

    private PipelineDefinition loadDefinition(String operationType) {
        return pipelineRepository.findActive(operationType)
                .orElseThrow(() -> new PipelineException("Nenhum pipeline ativo encontrado para: " + operationType));
    }
}
