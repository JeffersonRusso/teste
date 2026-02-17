package br.com.orquestrator.orquestrator.tasks.interceptor.error;

import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;
import org.springframework.stereotype.Component;

@Component
public class NodeIgnoreStrategy implements ErrorIgnoreStrategy {
    @Override
    public boolean shouldIgnore(Throwable e, ErrorHandlerConfig config) {
        if (e instanceof PipelineException pe && pe.getNodeId() != null) {
            return config.ignoreNodes().contains(pe.getNodeId());
        }
        return false;
    }
}
