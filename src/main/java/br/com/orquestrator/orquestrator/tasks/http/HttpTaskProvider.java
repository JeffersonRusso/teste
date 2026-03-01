package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.core.engine.binding.TaskBindingResolver;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.TaskProvider;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class HttpTaskProvider implements TaskProvider {

    private final RestClient restClient;
    private final TaskBindingResolver taskBindingResolver;

    @Override
    public String getType() {
        return "HTTP";
    }

    @Override
    public Task create(TaskDefinition def) {
        return () -> {
            // Resolve a configuração diretamente contra o contexto soberano
            var resolvedConfig = taskBindingResolver.resolve(def.config(), HttpTaskConfiguration.class);
            return new HttpTask(restClient, resolvedConfig).execute();
        };
    }
}
