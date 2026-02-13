package br.com.orquestrator.orquestrator.tasks.http;

import java.util.Map;

/**
 * Representa a configuração imutável e tipada de uma HttpTask.
 * Totalmente desacoplada de infraestrutura de JSON (Jackson).
 */
public record HttpTaskConfiguration(
    String urlTemplate,
    String method,
    String bodyTemplate,
    Map<String, String> headersTemplates
) {}
