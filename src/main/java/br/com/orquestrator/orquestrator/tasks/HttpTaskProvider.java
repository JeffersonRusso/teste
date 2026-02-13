package br.com.orquestrator.orquestrator.tasks;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.tasks.base.Task;
import br.com.orquestrator.orquestrator.tasks.http.HttpExecutor;
import br.com.orquestrator.orquestrator.tasks.http.HttpRequestFactory;
import br.com.orquestrator.orquestrator.tasks.http.HttpTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HttpTaskProvider implements TaskProvider {

    private final HttpRequestFactory requestFactory;
    private final HttpExecutor executor;

    @Override
    public String getType() {
        return "HTTP";
    }

    @Override
    public Task create(TaskDefinition def) {
        return new HttpTask(def, requestFactory, executor);
    }
}
