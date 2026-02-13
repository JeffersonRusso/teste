package br.com.orquestrator.orquestrator.tasks.http;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Representa uma requisição HTTP imutável na pipeline do orquestrador.
 * Utilizamos TreeMap com CASE_INSENSITIVE_ORDER para garantir conformidade com a RFC 7230.
 */
public record OrchestratorRequest(
        String method,
        URI uri,
        Map<String, String> headers,
        String body,
        long timeoutMs
) {
    /**
     * Construtor canônico protegido para garantir que os headers sejam sempre
     * tratados de forma case-insensitive e fiquem protegidos contra mutação externa.
     */
    public OrchestratorRequest {
        // Garantimos que o Map de headers não seja nulo e ignore Case
        Map<String, String> caseInsensitiveMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (headers != null) {
            caseInsensitiveMap.putAll(headers);
        }
        headers = Collections.unmodifiableMap(caseInsensitiveMap);
    }

    public OrchestratorRequest(String method, URI uri, long timeoutMs) {
        this(method, uri, new TreeMap<>(String.CASE_INSENSITIVE_ORDER), null, timeoutMs);
    }

    /**
     * Verifica a existência de um header de forma segura (Case Insensitive).
     */
    public boolean hasHeaderIgnoreCase(String key) {
        return headers.containsKey(key);
    }

    /**
     * Retorna uma NOVA instância com o header adicionado, mantendo a imutabilidade do record.
     * Segue o padrão 'Wither' para objetos imutáveis.
     */
    public OrchestratorRequest withHeader(String key, String value) {
        Map<String, String> newHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        newHeaders.putAll(this.headers);
        newHeaders.put(key, value);
        return new OrchestratorRequest(method, uri, newHeaders, body, timeoutMs);
    }
}