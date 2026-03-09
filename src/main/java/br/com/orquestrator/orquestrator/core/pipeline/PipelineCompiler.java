package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import br.com.orquestrator.orquestrator.infra.Flow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PipelineCompiler: Transforma definições em executáveis usando um DAG de compilação.
 * Agora usa Strings puras para os outputs requeridos.
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
        return Flow.start(new CompilationSession(def, activeTags))
                .next(this::runCompilationSteps)
                .finish(this::buildFinalPipeline);
    }

    private CompilationSession runCompilationSteps(CompilationSession session) {
        for (CompilationStep step : steps) {
            session = step.execute(session);
        }
        return session;
    }

    private Pipeline buildFinalPipeline(CompilationSession session) {
        PipelineDefinition def = session.getDefinition();

        Set<String> requiredPaths = def.defaultRequiredOutputs() != null 
            ? new HashSet<>(def.defaultRequiredOutputs()) 
            : Set.of();

        return new Pipeline(
            session.getNodes(), 
            Duration.ofMillis(def.timeoutMs()), 
            requiredPaths
        );
    }
}
