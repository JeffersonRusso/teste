package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.FlowConfigProvider;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.config.AppConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Estrategista de roteamento de fluxos.
 * Decide a versão do fluxo a ser executada e entrega a definição completa (Mapa do Caminho).
 * Suporta estratégias de Canary Release e Versionamento.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FlowRouter {

    private final AppConfigService appConfigService;
    private final FlowConfigProvider flowConfigProvider;
    private final ObjectMapper objectMapper;

    /**
     * Rota a operação para a definição de fluxo correta, aplicando regras de Canary.
     * Java 21: Retorna a FlowDefinition completa, encapsulando a complexidade de roteamento.
     */
    public FlowDefinition route(String operationType) {
        // 1. Resolve a versão ideal (Canary ou Default)
        Integer version = resolveVersion(operationType);
        
        log.debug("Roteando operação [{}] para versão [{}]", 
                operationType, version != null ? version : "LATEST");

        // 2. Obtém a definição completa do fluxo
        return flowConfigProvider.getFlow(operationType, version)
                .orElseThrow(() -> new PipelineException(STR."Fluxo não encontrado para: \{operationType} (v\{version})"));
    }

    private Integer resolveVersion(String operationType) {
        return parseConfig(operationType)
                .map(config -> resolveByStrategy(config, operationType))
                .orElse(null);
    }

    private Integer resolveByStrategy(FlowRoutingConfig config, String operationType) {
        if (config.canary() != null && isCanaryActive(config.canary().getPercentage())) {
            return config.canary().getVersion();
        }
        return config.getDefaultVersion();
    }

    private boolean isCanaryActive(int percentage) {
        if (percentage <= 0) return false;
        if (percentage >= 100) return true;
        return ThreadLocalRandom.current().nextInt(100) < percentage;
    }

    private Optional<FlowRoutingConfig> parseConfig(String operationType) {
        JsonNode rawConfig = appConfigService.getConfig(STR."routing.\{operationType}");
        if (rawConfig == null || rawConfig.isMissingNode()) {
            rawConfig = appConfigService.getConfig(operationType);
        }

        if (rawConfig == null || rawConfig.isMissingNode()) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.treeToValue(rawConfig, FlowRoutingConfig.class));
        } catch (Exception e) {
            log.error("Erro no roteamento de [{}]: {}", operationType, e.getMessage());
            return Optional.empty();
        }
    }
}
