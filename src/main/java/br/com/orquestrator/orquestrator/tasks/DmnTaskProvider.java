package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.base.TypedTaskProvider;
import br.com.orquestrator.orquestrator.tasks.script.dmn.DmnTask;
import br.com.orquestrator.orquestrator.tasks.script.dmn.DmnTaskConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provedor de DmnTask: Padronizado e com cache de decisões compiladas.
 */
@Component
public class DmnTaskProvider extends TypedTaskProvider<DmnTaskConfiguration> {

    private final DmnEngine dmnEngine;
    private final Map<String, DmnDecision> decisionCache = new ConcurrentHashMap<>();

    public DmnTaskProvider(ObjectMapper objectMapper, DmnEngine dmnEngine) {
        super(objectMapper, DmnTaskConfiguration.class, "DMN");
        this.dmnEngine = dmnEngine;
    }

    @Override
    protected Task createInternal(TaskDefinition def, DmnTaskConfiguration config) {
        String cacheKey = STR."\{config.dmnFile()}:\{config.decisionKey()}";
        DmnDecision decision = decisionCache.computeIfAbsent(cacheKey, _ -> 
            loadDecision(config.dmnFile(), config.decisionKey(), def.getNodeId().value())
        );
        
        return new DmnTask(def, dmnEngine, config, decision);
    }

    private DmnDecision loadDecision(String dmnFile, String decisionKey, String nodeId) {
        String path = "/dmn/" + dmnFile;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new TaskConfigurationException(STR."Arquivo DMN não encontrado: \{path} para a task: \{nodeId}");
            }
            return dmnEngine.parseDecision(decisionKey, is);
        } catch (Exception e) {
            throw new TaskConfigurationException(STR."Erro ao carregar DMN para a task: \{nodeId}", e);
        }
    }
}
