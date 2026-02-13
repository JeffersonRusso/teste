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
    private final GroovyScriptLoader scriptLoader;
    private final GroovyTaskConfiguration config;

    public GroovyTask(TaskDefinition definition,
                      GroovyScriptLoader scriptLoader,
                      GroovyBindingFactory bindingFactory,
                      TaskResultMapper resultMapper,
                      GroovyTaskConfiguration config) {
        super(definition);
        this.scriptLoader = scriptLoader;
        this.bindingFactory = bindingFactory;
        this.resultMapper = resultMapper;
        this.config = config;
    }

    @Override
    public void validateConfig() {
        if ((config.scriptName() == null || config.scriptName().isBlank()) && 
            (config.scriptBody() == null || config.scriptBody().isBlank())) {
            throw new PipelineException("Configuração inválida: scriptName ou scriptBody deve ser fornecido.");
        }
    }

    @Override
    public void execute(TaskData data) {
        try {
            // Carrega a classe do script (pode vir do cache)
            Class<? extends Script> scriptClass = loadScriptClass();
            
            data.addMetadata("scriptIdentifier", config.scriptName() != null ? config.scriptName() : "inline");

            // Cria o binding a partir do TaskData (respeitando o contrato)
            Binding binding = bindingFactory.createBinding(data, definition);

            log.debug("   [GroovyTask] Executando script: {}", definition.getNodeId().value());

            // Java 21: Instanciação moderna
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

    private Class<? extends Script> loadScriptClass() {
        // O loader ainda é necessário para gerenciar o cache de compilação
        if (config.scriptBody() != null) {
            return scriptLoader.loadFromSource("inline:" + definition.getNodeId().value(), config.scriptBody());
        }
        return scriptLoader.loadFromFile(config.scriptName());
    }
}
