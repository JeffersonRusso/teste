package br.com.orquestrator.orquestrator.tasks.triplification;

import br.com.orquestrator.orquestrator.api.task.Task;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.api.task.TaskStatus;
import br.com.orquestrator.orquestrator.core.context.TestContext;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Task para converter dados de entrada em um modelo RDF (Triplificação).
 */
public class TriplificationTask implements Task {

    private static final Logger log = LoggerFactory.getLogger(TriplificationTask.class);

    private final String baseUri;
    private final DataFactory dataFactory;

    public TriplificationTask(String baseUri, DataFactory dataFactory) {
        this.baseUri = baseUri;
        this.dataFactory = dataFactory;
    }

    @Override
    public TaskResult execute(TestContext context) {
        log.info("Iniciando tarefa de triplificação...");

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("fraud", baseUri);

        // Supondo que os dados a serem triplificados estão no contexto
        Map<String, Object> inputData = context.getVariables();

        // Cria um recurso principal para esta transação/análise
        Resource transactionResource = model.createResource(baseUri + "transaction/" + context.getId());

        inputData.forEach((key, value) -> {
            if (value != null) {
                var property = model.createProperty(baseUri, key);
                transactionResource.addProperty(property, value.toString());
            }
        });

        log.info("Modelo RDF criado com {} triplas.", model.size());

        // Armazena o modelo no contexto para tarefas futuras
        // Nota: O modelo precisa ser serializado ou armazenado de forma acessível
        // Por simplicidade, vamos colocar o modelo diretamente, mas isso pode precisar de ajustes
        context.set("rdfModel", model);

        return new TaskResult(TaskStatus.COMPLETED);
    }
}
