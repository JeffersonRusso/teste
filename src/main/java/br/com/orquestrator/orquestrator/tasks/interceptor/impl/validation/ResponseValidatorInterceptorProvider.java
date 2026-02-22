package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ResponseValidatorConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseValidatorInterceptorProvider implements InterceptorProvider<ResponseValidatorConfig> {

    private final ResponseValidatorInterceptor interceptor;

    @Override
    public String featureType() {
        return "RESPONSE_VALIDATOR";
    }

    @Override
    public Class<ResponseValidatorConfig> configClass() {
        return ResponseValidatorConfig.class;
    }

    @Override
    public TaskInterceptor create(ResponseValidatorConfig config, String nodeId) {
        return interceptor.adapt(config, nodeId);
    }
}
