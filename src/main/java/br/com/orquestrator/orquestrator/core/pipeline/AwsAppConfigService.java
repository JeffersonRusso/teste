package br.com.orquestrator.orquestrator.core.pipeline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AwsAppConfigService implements ConfigurationSource {

    @Override
    public Optional<InitializationPlan> fetch(String operationType) {
        log.debug("Buscando configuracao no AWS AppConfig (Fake) para: {}", operationType);
        
        // Simulando que o AppConfig só tem config para 'STANDARD_RISK' no momento
        if ("STANDARD_RISK".equals(operationType)) {
            return Optional.of(new InitializationPlan(operationType, List.of(
                new InitializationPlan.InitializerDefinition("customerDataInitializer", 1)
            )));
        }
        
        return Optional.empty();
    }

    @Override
    public int getPriority() {
        return 1; // Prioridade Máxima
    }
}