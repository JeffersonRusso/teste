package br.com.orquestrator.orquestrator.core.pipeline.steps;

import br.com.orquestrator.orquestrator.core.pipeline.CompilationSession;
import br.com.orquestrator.orquestrator.core.pipeline.CompilationStep;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class ScenarioFilterStep implements CompilationStep {
    @Override public int getOrder() { return 10; }

    @Override
    public CompilationSession execute(CompilationSession session) {
        Set<String> effectiveTags = (session.getActiveTags() == null || session.getActiveTags().isEmpty()) 
            ? Set.of("default") : session.getActiveTags();

        var filtered = session.getTasks().stream()
                .filter(t -> t.activationTags() == null || t.activationTags().isEmpty() || !Collections.disjoint(t.activationTags(), effectiveTags))
                .toList();
        
        session.setTasks(filtered);
        return session;
    }
}
