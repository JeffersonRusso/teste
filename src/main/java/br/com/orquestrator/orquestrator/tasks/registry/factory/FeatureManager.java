package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.domain.model.FeaturePhases;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Especialista no ciclo de vida das features.
 * Gerencia a mesclagem entre perfis de infraestrutura e configurações locais,
 * além de expandir referências globais.
 * Java 21: Utiliza SequencedCollections e imutabilidade.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeatureManager {

    private final ObjectMapper objectMapper;
    private final FeatureResolver featureResolver;

    /**
     * Mescla as features da task com as do perfil e resolve todos os templates.
     */
    public FeaturePhases mergeAndResolve(FeaturePhases taskFeat, JsonNode profileJson, String taskId) {
        FeaturePhases merged = merge(taskFeat, profileJson, taskId);
        return resolveTemplates(merged);
    }

    private FeaturePhases merge(FeaturePhases taskFeat, JsonNode profileJson, String taskId) {
        try {
            FeaturePhases profileFeatures = profileJson != null ? 
                objectMapper.treeToValue(profileJson, FeaturePhases.class) : null;

            if (profileFeatures == null) {
                return taskFeat != null ? taskFeat : new FeaturePhases(null, null, null);
            }

            // Java 21: Construção de listas imutáveis após o merge
            return new FeaturePhases(
                combine(profileFeatures.monitors(), taskFeat != null ? taskFeat.monitors() : null),
                combine(profileFeatures.preExecution(), taskFeat != null ? taskFeat.preExecution() : null),
                combine(profileFeatures.postExecution(), taskFeat != null ? taskFeat.postExecution() : null)
            );
            
        } catch (Exception e) {
            log.error(STR."Erro ao mesclar features para task \{taskId}: \{e.getMessage()}");
            return taskFeat != null ? taskFeat : new FeaturePhases(null, null, null);
        }
    }

    private List<FeatureDefinition> combine(List<FeatureDefinition> base, List<FeatureDefinition> override) {
        List<FeatureDefinition> combined = new ArrayList<>();
        if (base != null) combined.addAll(base);
        if (override != null) combined.addAll(override);
        return List.copyOf(combined);
    }

    private FeaturePhases resolveTemplates(FeaturePhases phases) {
        return new FeaturePhases(
            resolveList(phases.monitors()),
            resolveList(phases.preExecution()),
            resolveList(phases.postExecution())
        );
    }

    private List<FeatureDefinition> resolveList(List<FeatureDefinition> list) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream()
                .map(featureResolver::resolve)
                .toList();
    }
}
