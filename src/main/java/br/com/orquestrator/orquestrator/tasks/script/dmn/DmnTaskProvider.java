package br.com.orquestrator.orquestrator.tasks.script.dmn;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class DmnTaskProvider implements TaskProvider {

    private final DmnEngine dmnEngine;
    private final TaskBindingResolver taskBindingResolver;
    private final Map<String, DmnDecision> decisionCache = new ConcurrentHashMap<>();

    @Override
    public String getType() {
        return "DMN";
    }

    @Override
    public Task create(TaskDefinition def) {
        // 1. Resolve a configuração (dmnFile, decisionKey)
        // Nota: Aqui resolvemos apenas o que é necessário para carregar o DMN
        var config = taskBindingResolver.resolve(def.config(), DmnTaskConfiguration.class);
        
        String cacheKey = config.dmnFile() + ":" + config.decisionKey();
        DmnDecision decision = decisionCache.computeIfAbsent(cacheKey, k -> 
            loadDecision(config.dmnFile(), config.decisionKey(), def.nodeId().value())
        );

        return () -> {
            // 2. Resolve os inputs dinâmicos do contexto no momento da execução
            // O DMN opera sobre o mapa de dados do contexto
            var context = ContextHolder.CONTEXT.get();
            return new DmnTask(dmnEngine, decision, context.getRoot()).execute();
        };
    }

    private DmnDecision loadDecision(String dmnFile, String decisionKey, String nodeId) {
        String path = "/dmn/" + dmnFile;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new TaskConfigurationException("Arquivo DMN não encontrado: " + path + " para a task: " + nodeId);
            }
            return dmnEngine.parseDecision(decisionKey, is);
        } catch (Exception e) {
            throw new TaskConfigurationException("Erro ao carregar DMN para a task: " + nodeId, e);
        }
    }
}
