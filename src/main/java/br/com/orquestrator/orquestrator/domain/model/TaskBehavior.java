package br.com.orquestrator.orquestrator.domain.model;

import java.util.Collections;
import java.util.Set;

/**
 * TaskBehavior: Define políticas de execução.
 */
public record TaskBehavior(
    boolean failFast,
    Set<String> tags,
    boolean isGlobal,
    String cron
) {
    public TaskBehavior {
        if (tags == null) tags = Set.of("default");
    }

    /**
     * Lei de Deméter: Decide se o comportamento atende às tags fornecidas.
     */
    public boolean matches(Set<String> activeTags) {
        if (activeTags == null || activeTags.isEmpty()) {
            return tags.contains("default");
        }
        return tags.stream().anyMatch(activeTags::contains);
    }

    public static TaskBehavior defaultBehavior() {
        return new TaskBehavior(true, Set.of("default"), false, null);
    }
}
