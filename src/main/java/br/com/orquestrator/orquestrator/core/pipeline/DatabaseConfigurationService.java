package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.InitializationPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseConfigurationService implements ConfigurationSource {

    private final InitializationPlanRepository repository;

    @Override
    public Optional<InitializationPlan> fetch(String operationType) {
        log.debug("Buscando configuracao no Banco de Dados (Contingencia) para: {}", operationType);

        return repository.findById(operationType)
                .map(entity -> new InitializationPlan(
                        entity.getOperationType(),
                        entity.getTasks().stream()
                                .map(initTask -> new InitializationPlan.InitializerDefinition(
                                        initTask.getId().getTaskId(), 
                                        initTask.getId().getTaskVersion()))
                                .toList()
                ));
    }

    @Override
    public int getPriority() {
        return 2; // Prioridade de Contingência
    }
}
