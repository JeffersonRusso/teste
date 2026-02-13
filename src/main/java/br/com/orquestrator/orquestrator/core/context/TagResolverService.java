package br.com.orquestrator.orquestrator.core.context;

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

import static br.com.orquestrator.orquestrator.domain.ContextKey.TAGS;

/**
 * Serviço responsável por identificar e atribuir tags dinâmicas ao contexto de execução.
 * As tags são utilizadas para roteamento, seleção de tasks e políticas de resiliência.
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
        resolveTags(context, operationType);
    }

    /**
     * Resolve todas as tags aplicáveis ao contexto atual através dos providers registrados.
     */
    public void resolveTags(ExecutionContext context, String operationType) {
        log.debug("Iniciando resolução de tags para a operação: {}", operationType);

        // Coleta tags de todos os providers de forma funcional e segura
        Set<String> resolvedTags = providers.stream()
                .flatMap(provider -> safeResolve(provider, context).stream())
                .collect(Collectors.toSet());

        // Garante a presença da tag padrão e consolida em uma lista imutável
        List<String> finalTags = Stream.concat(Stream.of(DEFAULT_TAG), resolvedTags.stream())
                .distinct()
                .toList(); // Java 16+ toList() retorna uma lista imutável

        context.put(TAGS, finalTags);
        
        log.debug("Tags identificadas para [{}]: {}", operationType, finalTags);
    }

    private Collection<String> safeResolve(TagProvider provider, ExecutionContext context) {
        try {
            return provider.resolveTags(context);
        } catch (Exception e) {
            log.error("Falha ao resolver tags no provider [{}]: {}", 
                    provider.getClass().getSimpleName(), e.getMessage());
            return Collections.emptySet();
        }
    }
}
