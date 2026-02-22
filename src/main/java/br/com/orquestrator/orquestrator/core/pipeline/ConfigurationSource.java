package br.com.orquestrator.orquestrator.core.pipeline;

import java.util.Optional;

public interface ConfigurationSource {
    Optional<InitializationPlan> fetch(String operationType);
    int getPriority(); // Menor valor = Maior prioridade
}