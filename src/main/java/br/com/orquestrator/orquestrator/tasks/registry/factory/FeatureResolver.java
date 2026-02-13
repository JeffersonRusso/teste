package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.FeatureCatalogProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Inteligência de Configuração: Resolve referências globais e realiza o merge de configurações.
 * Utiliza o FeatureCatalogProvider para acesso ultra-rápido a templates em memória.
 * Java 21: Utiliza String Templates e lógica de Deep Merge recursiva.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeatureResolver {

    private final FeatureCatalogProvider catalogProvider;

    /**
     * Resolve uma feature: se tiver referência a um template, busca o global e faz o merge.
     */
    public FeatureDefinition resolve(FeatureDefinition localFeat) {
        // 1. Se não houver referência a template, a config local é a soberana
        if (!StringUtils.hasText(localFeat.templateRef())) {
            return localFeat;
        }

        // 2. Busca a definição global (template) no catálogo em memória
        return getTemplate(localFeat.templateRef())
                .map(global -> mergeDefinitions(global, localFeat))
                .orElseGet(() -> {
                    log.warn(STR."Template de Feature '\{localFeat.templateRef()}' não encontrado no catálogo.");
                    return localFeat;
                });
    }

    /**
     * Busca o template no catálogo.
     */
    private Optional<FeatureDefinition> getTemplate(String templateId) {
        return catalogProvider.getTemplate(templateId)
                .map(t -> new FeatureDefinition(t.getFeatureType(), templateId, t.getConfig()));
    }

    /**
     * Mescla as configurações: Valores locais sobrescrevem os globais (Deep Merge).
     */
    private FeatureDefinition mergeDefinitions(FeatureDefinition global, FeatureDefinition local) {
        try {
            // Se uma das configs for nula ou ausente, evita o merge caro
            if (global.config() == null || global.config().isMissingNode()) return local;
            if (local.config() == null || local.config().isMissingNode()) {
                return new FeatureDefinition(global.type(), local.templateRef(), global.config());
            }

            // Deep Merge usando Jackson: Prioridade para o 'local'
            JsonNode mergedConfig = global.config().deepCopy();
            deepMerge(mergedConfig, local.config());

            return new FeatureDefinition(
                local.type() != null ? local.type() : global.type(),
                local.templateRef(),
                mergedConfig
            );
        } catch (Exception e) {
            log.error("Falha ao mesclar configurações de feature: {}", e.getMessage());
            return local;
        }
    }

    /**
     * Realiza o merge profundo de dois nós JSON de forma recursiva.
     * Campos no 'updateNode' sobrescrevem campos no 'mainNode'.
     */
    private void deepMerge(JsonNode mainNode, JsonNode updateNode) {
        if (updateNode.isObject() && mainNode.isObject()) {
            updateNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                
                if (mainNode.has(key) && mainNode.get(key).isObject() && value.isObject()) {
                    deepMerge(mainNode.get(key), value);
                } else {
                    ((ObjectNode) mainNode).set(key, value);
                }
            });
        }
    }
}
