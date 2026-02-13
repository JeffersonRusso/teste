package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.infra.el.EvaluationContext;
import br.com.orquestrator.orquestrator.infra.el.ExpressionService;
import br.com.orquestrator.orquestrator.tasks.base.TaskData;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpRequestFactory {

    private final HttpHeaderManager headerManager;
    private final HttpBodyBuilder bodyBuilder;
    private final ExpressionService expressionService;

    public OrchestratorRequest create(String configUrl, 
                                      String method, 
                                      long timeoutMs, 
                                      JsonNode bodyConfig, 
                                      JsonNode headersConfig, 
                                      TaskData data) {
        
        // Criamos o contexto de avaliação a partir do TaskData (respeitando o contrato)
        EvaluationContext evalContext = expressionService.create(data);
        
        String finalUrl = evalContext.resolve(configUrl, String.class);

        OrchestratorRequest request = new OrchestratorRequest(method, URI.create(finalUrl), timeoutMs);

        headerManager.apply(request, headersConfig, evalContext);
        String body = bodyBuilder.buildBody(bodyConfig, evalContext);
        
        return new OrchestratorRequest(
            request.method(),
            request.uri(),
            request.headers(),
            body,
            request.timeoutMs()
        );
    }
}
