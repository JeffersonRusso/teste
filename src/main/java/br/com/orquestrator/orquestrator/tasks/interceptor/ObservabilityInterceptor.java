package br.com.orquestrator.orquestrator.tasks.interceptor;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.TaskChain;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Interceptor responsável por enriquecer o TaskData com métricas e logs contextuais.
 * Demonstra o padrão de "Feature" desacoplada do Runner.
 */
@Slf4j
@Component("OBSERVABILITY")
public class ObservabilityInterceptor implements TaskInterceptor {

    @Override
    public void intercept(TaskData data, TaskChain next, Object config, TaskDefinition taskDef) {
        String nodeId = taskDef.getNodeId().value();
        Instant start = Instant.now();
        
        // Enriquecimento pré-execução
        data.addMetadata("execution.start_time", start.toString());
        data.addMetadata("task.type", taskDef.getType());
        data.addMetadata("task.criticality", taskDef.getCriticality());

        try {
            log.debug("Iniciando execução da task [{}]", nodeId);
            
            next.proceed(data);
            
            long duration = Instant.now().toEpochMilli() - start.toEpochMilli();
            data.addMetadata("execution.duration_ms", duration);
            
            log.debug("Task [{}] finalizada com sucesso em {}ms", nodeId, duration);

        } catch (Exception e) {
            // Enriquecimento em caso de erro
            data.addMetadata("execution.error_type", e.getClass().getSimpleName());
            data.addMetadata("execution.error_message", e.getMessage());
            
            log.error("Falha na execução da task [{}]. Erro: {}", nodeId, e.getMessage());
            throw e; // Propaga para o TaskRunner tratar (fail-fast, etc)
        } finally {
            data.addMetadata("execution.end_time", Instant.now().toString());
        }
    }
}
