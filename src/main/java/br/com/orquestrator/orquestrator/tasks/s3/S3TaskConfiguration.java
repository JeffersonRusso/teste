package br.com.orquestrator.orquestrator.tasks.s3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record S3TaskConfiguration(
    String bucket,
    String key,
    String region,
    Object content // O conteúdo a ser enviado (pode ser um template SpEL)
) {}
