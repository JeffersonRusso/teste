package br.com.orquestrator.orquestrator.infra.config;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.FeatureCatalogProvider;
import br.com.orquestrator.orquestrator.infra.cache.GlobalTaskScheduler;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistryWarmup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Orquestrador de Inicialização (Bootstrapper).
 * Garante que o sistema seja inicializado em uma sequência síncrona e segura,
 * evitando condições de corrida entre o banco de dados, o catálogo e o agendador.
 * Java 21: Utiliza String Templates e execução sequencial garantida.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrchestratorBootstrapper {

    private final FeatureCatalogProvider catalogProvider;
    private final TaskRegistryWarmup taskWarmup;
    private final GlobalTaskScheduler globalScheduler;

    /**
     * Ponto de entrada único para a inicialização do sistema após o contexto estar pronto.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Iniciando sequência de boot sincronizada do Orquestrador...");

        try {
            // 1. Carrega Templates e Perfis (Infraestrutura de suporte)
            catalogProvider.loadInitialData();

            // 2. Carrega e Instancia as Tasks (Domínio e Cache de Execução)
            taskWarmup.warmup();

            // 3. Inicia Agendamentos Globais (Execução de tarefas de sistema)
            globalScheduler.initialize();

            log.info("Sequência de boot concluída com sucesso. Orquestrador pronto para receber requisições.");
        } catch (Exception e) {
            log.error(STR."FALHA CRÍTICA NA INICIALIZAÇÃO DO SISTEMA: \{e.getMessage()}", e);
            // Em um cenário real, poderíamos decidir encerrar a aplicação aqui
        }
    }
}
