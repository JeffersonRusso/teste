package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.Flow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PipelineCompiler: Transforma definições em executáveis usando um DAG de compilação.
 * SOLID: Aberto para novos passos de compilação sem alterar o motor.
 */
@Slf4j
@Component
public class PipelineCompiler {

    private final List<CompilationStep> steps;

    public PipelineCompiler(List<CompilationStep> steps) {
        this.steps = steps.stream()
                .sorted(Comparator.comparingInt(CompilationStep::getOrder))
                .toList();
    }

    public Pipeline compile(PipelineDefinition def, Set<String> activeTags) {
        // Inicia o fluxo de compilação (DAG de Bolinhas)
        return Flow.start(new CompilationSession(def, activeTags))
                .next(this::runCompilationSteps)
                .finish(this::buildFinalPipeline);
    }

    private CompilationSession runCompilationSteps(CompilationSession session) {
        // Percorre as bolinhas de compilação
        for (CompilationStep step : steps) {
            session = step.execute(session);
        }
        return session;
    }

    private Pipeline buildFinalPipeline(CompilationSession session) {
        PipelineDefinition def = session.getDefinition();

        Set<DataPath> requiredPaths = def.defaultRequiredOutputs().stream()
                .map(DataPath::of)
                .collect(Collectors.toSet());

        return new Pipeline(
            session.getNodes(), 
            Duration.ofMillis(def.timeoutMs()), 
            requiredPaths,
            session.getNormalizationPlan()
        );
    }
}
