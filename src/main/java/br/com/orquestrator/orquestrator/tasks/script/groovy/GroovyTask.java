package br.com.orquestrator.orquestrator.tasks.script.groovy;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.base.AbstractTask;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import br.com.orquestrator.orquestrator.tasks.common.TaskResultMapper;
import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroovyTask extends AbstractTask {

    private final GroovyBindingFactory bindingFactory;
    private final TaskResultMapper resultMapper;
    private final Class<? extends Script> scriptClass;

    public GroovyTask(TaskDefinition definition, 
                      GroovyScriptLoader scriptLoader,
                      GroovyBindingFactory bindingFactory,
                      TaskResultMapper resultMapper) {
        super(definition);
        this.bindingFactory = bindingFactory;
        this.resultMapper = resultMapper;
        scriptLoader.validateConfig(definition.getConfig(), definition.getNodeId());
        this.scriptClass = scriptLoader.load(definition.getConfig(), definition.getNodeId());
    }

    @Override
    public void validateConfig() {
    }

    @Override
    public void execute(TaskData data) {
        try {
            data.addMetadata("scriptName", scriptClass.getSimpleName());

            // Cria o binding a partir do TaskData (respeitando o contrato)
            Binding binding = bindingFactory.createBinding(data, definition);

            log.debug("   [GroovyTask] Executando script: {}", definition.getNodeId().value());

            Script script = scriptClass.getConstructor(Binding.class).newInstance(binding);
            Object result = script.run();

            // Mapeia o resultado de volta para o TaskData
            resultMapper.mapResult(data, result, definition);

        } catch (Exception e) {
            String nodeId = definition.getNodeId().value();
            log.error("ERRO: Script {} falhou: {}", nodeId, e.getMessage());
            throw new PipelineException("Erro na execução do script Groovy: " + nodeId, e)
                    .withNodeId(nodeId);
        }
    }
}
