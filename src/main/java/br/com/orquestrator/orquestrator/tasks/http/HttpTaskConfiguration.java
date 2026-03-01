package br.com.orquestrator.orquestrator.tasks.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HttpTaskConfiguration(
    String url,
    String method,
    Map<String, String> headers,
    Object body // Agora recebe o objeto real resolvido
) {}
