package br.com.orquestrator.orquestrator.core.context;

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
 * Decide a versão do fluxo a ser executada, suportando estratégias de Canary Release.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FlowRouter {

    private final AppConfigService appConfigService;
    private final ObjectMapper objectMapper;

    /**
     * Resolve a versão do fluxo para o tipo de operação fornecido.
     */
    public Integer resolveVersion(String operationType) {
        return parseConfig(operationType)
                .map(config -> resolveFromConfig(config, operationType))
                .orElse(null);
    }

    private Optional<FlowRoutingConfig> parseConfig(String operationType) {
        JsonNode rawConfig = appConfigService.getConfig(operationType);
        
        if (rawConfig == null || rawConfig.isMissingNode()) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.treeToValue(rawConfig, FlowRoutingConfig.class));
        } catch (Exception e) {
            log.error("Falha ao parsear configuração de roteamento para [{}]: {}", operationType, e.getMessage());
            return Optional.empty();
        }
    }

    private Integer resolveFromConfig(FlowRoutingConfig config, String operationType) {
        if (config.canary() != null && shouldRouteToCanary(config.canary().getPercentage())) {
            int canaryVersion = config.canary().getVersion();
            log.info("Roteamento CANARY ativado para [{}]: Direcionando para v{}", operationType, canaryVersion);
            return canaryVersion;
        }

        return config.getDefaultVersion();
    }

    private boolean shouldRouteToCanary(int percentage) {
        if (percentage <= 0) return false;
        if (percentage >= 100) return true;
        return ThreadLocalRandom.current().nextInt(100) < percentage;
    }
}
