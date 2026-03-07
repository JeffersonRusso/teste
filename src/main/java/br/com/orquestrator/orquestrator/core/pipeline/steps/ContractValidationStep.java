package br.com.orquestrator.orquestrator.core.pipeline.steps;

import br.com.orquestrator.orquestrator.core.engine.validation.ContractValidator;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationSession;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractValidationStep implements CompilationStep {

    private final ContractValidator contractValidator;

    @Override public int getOrder() { return 25; }

    @Override
    public CompilationSession execute(CompilationSession session) {
        contractValidator.validate(session.getDefinition(), session.getTasks());
        return session;
    }
}
