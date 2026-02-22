package br.com.orquestrator.orquestrator.tasks.http;

import java.net.URI;
import java.util.Map;

/**
 * Representa uma requisição HTTP imutável.
 * Otimizado para Alocação Zero: Removemos o TreeMap case-insensitive que gerava lixo massivo.
 */
public record OrchestratorRequest(
        String method,
        URI uri,
        Map<String, String> headers,
        String body,
        long timeoutMs
) {
    public String bodyOrEmpty() {
        return body != null ? body : "";
    }
}
