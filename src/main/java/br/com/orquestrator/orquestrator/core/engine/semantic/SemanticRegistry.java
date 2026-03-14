package br.com.orquestrator.orquestrator.core.engine.semantic;

import br.com.orquestrator.orquestrator.domain.model.SemanticHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SemanticRegistry: Gerencia os manipuladores de tipos semânticos do sistema.
 */
@Slf4j
@Component
public class SemanticRegistry {

    private final Map<String, SemanticHandler> handlers;

    public SemanticRegistry(List<SemanticHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                    h -> h.getTypeName().toUpperCase(),
                    Function.identity()
                ));
    }

    public Optional<SemanticHandler> getHandler(String typeName) {
        if (typeName == null) return Optional.empty();
        return Optional.ofNullable(handlers.get(typeName.toUpperCase()));
    }
}
