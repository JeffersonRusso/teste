package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.infra.json.MapBuilder;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import br.com.orquestrator.orquestrator.tasks.script.dmn.DmnTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DmnTaskProvider implements TaskProvider {

    private final DmnEngine dmnEngine;
    private final TaskResultMapper resultMapper;
    private final ObjectMapper objectMapper;
    private final MapBuilder mapBuilder;

    @Override
    public String getType() {
        return "DMN";
    }

    @Override
    public Task create(TaskDefinition definition) {
        return new DmnTask(definition, dmnEngine, resultMapper, objectMapper, mapBuilder);
    }
}
