package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;

public interface ContextInitializer {
    void initialize(ExecutionContext context, String operationType);
}
