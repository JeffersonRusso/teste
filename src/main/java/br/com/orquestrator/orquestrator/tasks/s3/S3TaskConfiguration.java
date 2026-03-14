package br.com.orquestrator.orquestrator.tasks.s3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * S3TaskConfiguration: Configuração para operações no S3.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record S3TaskConfiguration(
    String operation, // Ex: "PUT", "GET", "DELETE"
    String bucket,
    String key,
    String region,
    Object content // O conteúdo a ser enviado (pode ser um template SpEL)
) {}
