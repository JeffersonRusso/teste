package br.com.orquestrator.orquestrator.tasks.s3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuração imutável e tipada para a S3Task.
 * Representa o contrato de exportação para o S3.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record S3TaskConfiguration(
    @JsonProperty("bucket") String bucket,
    @JsonProperty("key") String keyTemplate,
    @JsonProperty("content") String contentExpression,
    @JsonProperty("region") String region,
    @JsonProperty("schema") String schemaRef
) {}
