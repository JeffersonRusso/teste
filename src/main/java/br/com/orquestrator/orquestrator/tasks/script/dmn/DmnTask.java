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
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DmnTask extends AbstractTask {

    private final DmnEngine dmnEngine;
    private final DmnDecision decision;
    private final TaskResultMapper resultMapper;
    private final ObjectMapper objectMapper;
    private final MapBuilder mapBuilder;

    public DmnTask(TaskDefinition definition, 
                   DmnEngine dmnEngine, 
                   TaskResultMapper resultMapper, 
                   ObjectMapper objectMapper,
                   MapBuilder mapBuilder) {
        super(definition);
        this.dmnEngine = dmnEngine;
        this.resultMapper = resultMapper;
        this.objectMapper = objectMapper;
        this.mapBuilder = mapBuilder;
        this.decision = loadDecision();
    }

    private DmnDecision loadDecision() {
        String dmnFile = definition.getConfig().path("dmnFile").asText();
        String decisionKey = definition.getConfig().path("decisionKey").asText();
        String path = STR."/dmn/\{dmnFile}";
        
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new IllegalArgumentException(STR."Arquivo DMN não encontrado: \{path}");
            return dmnEngine.parseDecision(decisionKey, is);
        } catch (Exception e) {
            throw new RuntimeException(STR."Erro ao carregar DMN: \{e.getMessage()}", e);
        }
    }

    @Override
    public void validateConfig() {
    }

    @Override
    public void execute(TaskData data) {
        log.debug("   [DmnTask] Executando decisão: {}", decision.getName());

        Map<String, Object> inputs = prepareHierarchicalInputs(data);

        try {
            DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, inputs);

            if (result.isEmpty()) {
                log.warn("   [DmnTask] Nenhuma regra satisfeita para: {}", decision.getName());
                return;
            }

            resultMapper.mapResult(data, result.getFirstResult().getEntryMap(), definition);
        } catch (Exception e) {
            log.error("Erro na avaliação DMN {}: {}", decision.getKey(), e.getMessage());
            throw e;
        }
    }

    private Map<String, Object> prepareHierarchicalInputs(TaskData data) {
        List<DataSpec> requires = definition.getRequires();
        Map<String, Object> inputs = new HashMap<>();
        
        if (requires == null) return inputs;

        for (int i = 0; i < requires.size(); i++) {
            String key = requires.get(i).name();
            // Otimização Crítica: Usamos unwrap() para passar o valor real ao DMN
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
