package br.com.orquestrator.orquestrator.tasks.s3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record S3TaskConfiguration(
    @JsonProperty("bucket") String bucket,
    @JsonProperty("key") String keyTemplate, // Suporta SpEL ou Template
    @JsonProperty("content") String contentExpression, // SpEL para selecionar o objeto a ser salvo
    @JsonProperty("schema") String schemaRef // Referência opcional a um schema de validação
) {}
