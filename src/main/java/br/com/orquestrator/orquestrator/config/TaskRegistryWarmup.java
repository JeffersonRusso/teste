package br.com.orquestrator.orquestrator.config;

import br.com.orquestrator.orquestrator.core.pipeline.compiler.PipelineCompiler;
import br.com.orquestrator.orquestrator.core.ports.output.PipelineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRegistryWarmup {

    private final PipelineRepository pipelineRepository;
    private final PipelineCompiler compiler;

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        log.info("Iniciando warm-up soberano dos pipelines...");
        Set<String> defaultTags = Collections.singleton("default");
        
        pipelineRepository.findAllActiveOperationTypes().forEach(op -> {
            try {
                pipelineRepository.findActive(op).ifPresent(definition -> {
                    compiler.compile(definition, defaultTags);
                    log.info("Pipeline [{}] aquecido com sucesso.", op);
                });
            } catch (Exception e) {
                log.error("Falha no warm-up do pipeline [{}]: {}", op, e.getMessage());
            }
        });
        log.info("Warm-up concluído.");
    }
}
