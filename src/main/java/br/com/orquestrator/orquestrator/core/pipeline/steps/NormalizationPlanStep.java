package br.com.orquestrator.orquestrator.core.pipeline.steps;

import br.com.orquestrator.orquestrator.core.engine.binding.NormalizationCompiler;
import br.com.orquestrator.orquestrator.core.engine.binding.NormalizationStep;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationSession;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NormalizationPlanStep implements CompilationStep {

    private final NormalizationCompiler normalizationCompiler;

    @Override public int getOrder() { return 50; }

    @Override
    public CompilationSession execute(CompilationSession session) {
        List<NormalizationStep> plan = normalizationCompiler.createPlan(session.getDefinition().inputMapping());
        session.setNormalizationPlan(plan);
        return session;
    }
}
