package br.com.orquestrator.orquestrator.tasks.interceptor.error;

import br.com.orquestrator.orquestrator.tasks.interceptor.config.ErrorHandlerConfig;

/**
 * Estratégia para decidir se um erro deve ser ignorado pelo ErrorHandler.
 */
public interface ErrorIgnoreStrategy {
    boolean shouldIgnore(Throwable e, ErrorHandlerConfig config);
}
