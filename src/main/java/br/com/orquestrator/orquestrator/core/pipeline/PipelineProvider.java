package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.core.context.init.ContextTaskInitializer;
import br.com.orquestrator.orquestrator.core.context.init.ContextInitializerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Busca o plano de execução de uma fonte de configuração externa (App Config, DB, etc.).
 * Mantém um cache das instâncias resolvidas para evitar overhead de lookup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineProvider {

    private final ContextInitializerRegistry initializerRegistry;
    private final List<ConfigurationSource> configSources;
    private final Map<String, List<ContextTaskInitializer>> resolvedCache = new ConcurrentHashMap<>();

    /**
     * Retorna a lista de inicializadores prontos para execução.
     */
    public List<ContextTaskInitializer> getResolvedInitializers(String operationType) {
        return resolvedCache.computeIfAbsent(operationType, this::resolvePipeline);
    }

    /**
     * Método chamado pelo listener do App Config quando uma configuração muda.
     */
    public void refreshPipeline(String operationType) {
        log.info("Atualizando cache de pipeline para operacao: {}", operationType);
        resolvedCache.remove(operationType);
    }

    private List<ContextTaskInitializer> resolvePipeline(String operationType) {
        log.debug("Montando pipeline para operacao: {}", operationType);
        
        InitializationPlan plan = configSources.stream()
                .sorted(Comparator.comparingInt(ConfigurationSource::getPriority))
                .map(source -> source.fetch(operationType))
                .flatMap(Optional::stream)
                .findFirst()
                .orElseGet(() -> new InitializationPlan(operationType, List.of()));

        return plan.initializers().stream()
                .map(def -> initializerRegistry.getInitializer(def.id()))
                .flatMap(Optional::stream)
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
