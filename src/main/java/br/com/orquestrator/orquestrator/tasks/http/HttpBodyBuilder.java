package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.exception.BodyResolutionException;
import br.com.orquestrator.orquestrator.infra.el.ConfigResolver;
import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpBodyBuilder {

    private final ConfigResolver configResolver;

    public String buildBody(JsonNode bodyConfig, EvaluationContext context) {
        try {
            return Optional.ofNullable(bodyConfig)
                    .filter(node -> !node.isNull() && !node.isMissingNode())
                    .map(node -> configResolver.resolve(node, context))
                    .map(JsonNode::toString)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Falha ao construir corpo JSON dinâmico", e);
            throw new BodyResolutionException("Erro na resolução do corpo da requisição", e);
        }
    }
}
