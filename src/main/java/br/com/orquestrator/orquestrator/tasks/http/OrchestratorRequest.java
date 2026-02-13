package br.com.orquestrator.orquestrator.tasks.http;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Representa uma requisição HTTP imutável na pipeline do orquestrador.
 * Otimizado para evitar alocações desnecessárias e garantir conformidade com a RFC 7230.
 * Java 21: Utiliza Records para imutabilidade e clareza.
 */
public record OrchestratorRequest(
        String method,
        URI uri,
        Map<String, String> headers,
        String body,
        long timeoutMs
) {
    /**
     * Construtor canônico: Garante a defesa dos dados e normalização de protocolos.
     */
    public OrchestratorRequest {
        // Garantimos que o Map de headers seja case-insensitive e imutável
        if (headers == null || headers.isEmpty()) {
            headers = Collections.emptyMap();
        } else {
            Map<String, String> caseInsensitiveMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            caseInsensitiveMap.putAll(headers);
            headers = Collections.unmodifiableMap(caseInsensitiveMap);
        }

        // Normalização para evitar falhas no HttpMethod.valueOf
        method = method != null ? method.toUpperCase() : "GET";
    }

    /**
     * Construtor de conveniência para requisições simples.
     */
    public OrchestratorRequest(String method, URI uri, long timeoutMs) {
        this(method, uri, Collections.emptyMap(), null, timeoutMs);
    }

    /**
     * Verifica a existência de um header de forma segura (Case Insensitive).
     */
    public boolean hasHeaderIgnoreCase(String key) {
        return headers.containsKey(key);
    }

    /**
     * Retorna uma NOVA instância com o header adicionado, mantendo a imutabilidade.
     * Otimização: Se o valor já for idêntico, retorna a própria instância (this).
     */
    public OrchestratorRequest withHeader(String key, String value) {
        if (value != null && value.equals(headers.get(key))) {
            return this;
        }

        Map<String, String> newHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        newHeaders.putAll(this.headers);
        newHeaders.put(key, value);
        return new OrchestratorRequest(method, uri, newHeaders, body, timeoutMs);
    }

    /**
     * Atalho útil para o Executor: Garante que o corpo nunca seja nulo na chamada do RestClient.
     */
    public String bodyOrEmpty() {
        return body != null ? body : "";
    }
}
