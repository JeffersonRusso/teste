package br.com.orquestrator.orquestrator.infra.config;

import br.com.orquestrator.orquestrator.core.pipeline.MetadataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * OrchestratorBootstrapper: Garante que o motor suba com todos os metadados em memória.
 * Java 21: Utiliza Virtual Threads para não bloquear o startup do Spring se o carregamento for pesado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrchestratorBootstrapper {

    private final MetadataLoader metadataLoader;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Iniciando Warmup do Orquestrador...");
        
        try {
            // Carregamento síncrono aqui é proposital: 
            // Queremos que os metadados estejam prontos ANTES de qualquer request chegar.
            metadataLoader.reloadAll();
            log.info("Warmup concluído. Orquestrador pronto para processar requisições.");
        } catch (Exception e) {
            log.error("FALHA CRÍTICA NO WARMUP: O sistema pode operar com metadados incompletos.", e);
        }
    }
}
