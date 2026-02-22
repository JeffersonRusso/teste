package br.com.orquestrator.orquestrator.tasks.interceptor.impl.validation;

import br.com.orquestrator.orquestrator.tasks.interceptor.api.InterceptorProvider;
import br.com.orquestrator.orquestrator.tasks.interceptor.api.TaskInterceptor;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.SchemaValidatorConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchemaValidatorInterceptorProvider implements InterceptorProvider<SchemaValidatorConfig> {

    private final SchemaValidatorInterceptor interceptor;

    @Override
    public String featureType() {
        return "SCHEMA_VALIDATOR";
    }

    @Override
    public Class<SchemaValidatorConfig> configClass() {
        return SchemaValidatorConfig.class;
    }

    @Override
    public TaskInterceptor create(SchemaValidatorConfig config, String nodeId) {
        return interceptor.adapt(config, nodeId);
    }
}
