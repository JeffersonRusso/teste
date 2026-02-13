package br.com.orquestrator.orquestrator.tasks.script.dmn;

import br.com.orquestrator.orquestrator.domain.model.DataSpec;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.json.MapBuilder;
import br.com.orquestrator.orquestrator.tasks.base.AbstractTask;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DmnTask extends AbstractTask {

    private final DmnEngine dmnEngine;
    private final TaskResultMapper resultMapper;
    private final ObjectMapper objectMapper;
    private final MapBuilder mapBuilder;
    private final DmnTaskConfiguration config;

    public DmnTask(TaskDefinition definition, 
                   DmnEngine dmnEngine, 
                   TaskResultMapper resultMapper, 
                   ObjectMapper objectMapper,
                   MapBuilder mapBuilder,
                   DmnTaskConfiguration config) {
        super(definition);
        this.dmnEngine = dmnEngine;
        this.resultMapper = resultMapper;
        this.objectMapper = objectMapper;
        this.mapBuilder = mapBuilder;
        this.config = config;
    }

    @Override
    public void validateConfig() {
        if (config.decision() == null) {
            throw new IllegalStateException("DMN Decision não foi carregada para a task: " + definition.getNodeId());
        }
    }

    @Override
    public void execute(TaskData data) {
        log.debug("   [DmnTask] Executando decisão: {}", config.decision().getName());

        Map<String, Object> inputs = prepareHierarchicalInputs(data);

        try {
            DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(config.decision(), inputs);

            if (result.isEmpty()) {
                log.warn("   [DmnTask] Nenhuma regra satisfeita para: {}", config.decision().getName());
                return;
            }

            resultMapper.mapResult(data, result.getFirstResult().getEntryMap(), definition);
        } catch (Exception e) {
            log.error("Erro na avaliação DMN {}: {}", config.decision().getKey(), e.getMessage());
            throw e;
        }
    }

    private Map<String, Object> prepareHierarchicalInputs(TaskData data) {
        List<DataSpec> requires = definition.getRequires();
        Map<String, Object> inputs = new HashMap<>();
        
        if (requires == null) return inputs;

        for (int i = 0; i < requires.size(); i++) {
            String key = requires.get(i).name();
            Object value = data.get(key).unwrap();
            
            if (value != null) {
                Object normalizedValue = (value instanceof JsonNode node) 
                        ? objectMapper.convertValue(node, Object.class) 
                        : value;
                
                mapBuilder.addNested(inputs, key, normalizedValue);
            }
        }
        return inputs;
    }
}
