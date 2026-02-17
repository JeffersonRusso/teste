package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.FlowConfigProvider;
import br.com.orquestrator.orquestrator.core.context.routing.RoutingStrategy;
import br.com.orquestrator.orquestrator.domain.model.FlowDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.config.AppConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * FlowRouter: Orquestrador de roteamento de fluxos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FlowRouter {

    private final AppConfigService appConfigService;
    private final FlowConfigProvider flowConfigProvider;
    private final ObjectMapper objectMapper;
    private final List<RoutingStrategy> strategies;

    public FlowDefinition route(String operationType) {
        Integer version = resolveVersion(operationType);
        
        return flowConfigProvider.getFlow(operationType, version)
                .orElseThrow(() -> new PipelineException(STR."Fluxo não encontrado: \{operationType} (v\{version})"));
    }

    private Integer resolveVersion(String operationType) {
        return parseConfig(operationType)
                .flatMap(config -> strategies.stream()
                        .sorted(Comparator.comparingInt(RoutingStrategy::getPriority))
                        .map(s -> s.resolveVersion(config))
                        .flatMap(Optional::stream)
                        .findFirst())
                .orElse(null);
    }

    private Optional<FlowRoutingConfig> parseConfig(String operationType) {
        JsonNode raw = appConfigService.getConfig(STR."routing.\{operationType}");
        if (raw == null || raw.isMissingNode()) raw = appConfigService.getConfig(operationType);
        if (raw == null || raw.isMissingNode()) return Optional.empty();

        try {
            return Optional.of(objectMapper.treeToValue(raw, FlowRoutingConfig.class));
        } catch (Exception e) {
            log.error(STR."Erro no parsing de roteamento [\{operationType}]: \{e.getMessage()}");
            return Optional.empty();
        }
    }
}
