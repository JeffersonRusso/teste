package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.infra.el.SpelContextFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.DecoratorFactory;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskDecorator;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ResponseValidatorConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseValidatorDecoratorFactory implements DecoratorFactory<ResponseValidatorConfig> {

    private final SpelContextFactory contextFactory;

    @Override
    public String getType() {
        return "RESPONSE_VALIDATOR";
    }

    @Override
    public Class<ResponseValidatorConfig> getConfigClass() {
        return ResponseValidatorConfig.class;
    }

    @Override
    public TaskDecorator create(ResponseValidatorConfig config, String nodeId) {
        return new ResponseValidatorDecorator(config, nodeId, contextFactory);
    }
}
