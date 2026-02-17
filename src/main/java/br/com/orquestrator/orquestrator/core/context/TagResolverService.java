package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.core.context.init.ContextInitializer;
import br.com.orquestrator.orquestrator.core.context.tag.TagProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TagResolverService: Especialista em resolução de tags.
 */
@Slf4j
@Service
@Order(2)
@RequiredArgsConstructor
public class TagResolverService implements ContextInitializer {

    private static final String DEFAULT_TAG = "ALL";
    private final List<TagProvider> providers;

    @Override
    public void initialize(ExecutionContext context, String operationType) {
        Set<String> resolvedTags = providers.stream()
                .flatMap(p -> safeResolve(p, context).stream())
                .collect(Collectors.toSet());

        List<String> finalTags = Stream.concat(Stream.of(DEFAULT_TAG), resolvedTags.stream())
                .distinct()
                .toList();

        context.put(ContextKey.TAGS, finalTags);
        log.debug("Tags resolvidas para [{}]: {}", operationType, finalTags);
    }

    private Collection<String> safeResolve(TagProvider provider, ExecutionContext context) {
        try {
            return provider.resolveTags(context);
        } catch (Exception e) {
            log.error(STR."Falha no TagProvider \{provider.getClass().getSimpleName()}: \{e.getMessage()}");
            return Collections.emptySet();
        }
    }
}
