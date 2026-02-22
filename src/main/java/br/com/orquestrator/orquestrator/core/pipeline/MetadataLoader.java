package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.FlowConfigRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.TaskCatalogProvider;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.repository.FlowConfigProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MetadataLoader: Responsável por carregar os dados dos adaptadores e atualizar o Store.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataLoader {

    private final TaskCatalogProvider taskProvider;
    private final FlowConfigProvider flowProvider;
    private final OrchestratorMetadataStore store;
    private final FlowConfigRepository flowRepository; // Para buscar todos os fluxos ativos

    public void reloadAll() {
        log.info("Iniciando recarregamento total de metadados...");
        
        // 1. Recarrega Tasks
        List<TaskDefinition> tasks = taskProvider.findAllActive();
        store.updateTasks(tasks);

        // 2. Recarrega Fluxos (Buscamos todos os tipos de operação ativos)
        // Nota: Precisamos de um método no provider para buscar todos, ou usamos o repository
        List<FlowDefinition> flows = flowRepository.findAllActiveOperations().stream()
                .map(op -> flowProvider.getFlow(op))
                .flatMap(java.util.Optional::stream)
                .toList();
        
        store.updateFlows(flows);
        
        log.info("Recarregamento de metadados concluído com sucesso.");
    }
}
