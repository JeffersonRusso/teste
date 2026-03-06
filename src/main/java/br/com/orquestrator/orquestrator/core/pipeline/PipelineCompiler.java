package br.com.orquestrator.orquestrator.core.pipeline;

import br.com.orquestrator.orquestrator.domain.model.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import br.com.orquestrator.orquestrator.domain.vo.Pipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        CompilationSession session = new CompilationSession(def, activeTags);

        for (CompilationStep step : steps) {
            step.execute(session);
        }

        // Pré-resolve os caminhos de saída para o caminho quente
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
