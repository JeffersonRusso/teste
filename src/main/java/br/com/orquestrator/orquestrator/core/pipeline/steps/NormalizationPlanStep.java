package br.com.orquestrator.orquestrator.core.pipeline.steps;

import br.com.orquestrator.orquestrator.core.engine.binding.DataMarshaller;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationSession;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NormalizationPlanStep implements CompilationStep {

    private final DataMarshaller marshaller;

    @Override public int getOrder() { return 50; } // Último passo

    @Override
    public void execute(CompilationSession session) {
        var plan = marshaller.createNormalizationPlan(session.getDefinition().inputMapping());
        session.setNormalizationPlan(plan);
    }
}
