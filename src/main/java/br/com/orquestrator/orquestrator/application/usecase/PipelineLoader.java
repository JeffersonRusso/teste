package br.com.orquestrator.orquestrator.application.usecase;

import br.com.orquestrator.orquestrator.core.pipeline.compiler.PipelineCompiler;
import br.com.orquestrator.orquestrator.core.ports.output.PipelineRepository;
import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * PipelineLoader: Carregador inteligente que cacheia o Grafo de Execução JÁ COMPILADO.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineLoader {

    private final PipelineRepository repository;
    private final PipelineCompiler compiler;

    @Cacheable(value = "compiled_pipelines", key = "#operationType + '_' + #tags.hashCode()")
    public Pipeline load(String operationType, Set<String> tags) {
        log.info("Carregando e compilando pipeline: {} (Tags: {})", operationType, tags);
        
        PipelineDefinition def = repository.findActive(operationType)
                .orElseThrow(() -> new PipelineException("Pipeline não encontrado: " + operationType));

        return compiler.compile(def, tags);
    }
}
