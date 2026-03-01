package br.com.orquestrator.orquestrator.tasks.s3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record S3TaskConfiguration(
    String bucket,
    String region,
    String key,
    Object content // Agora recebe o dado real resolvido
) {}
