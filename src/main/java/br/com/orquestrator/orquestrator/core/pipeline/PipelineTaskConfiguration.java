package br.com.orquestrator.orquestrator.core.pipeline;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PipelineTaskConfiguration(
    @JsonAlias("operation_type")
    String operationType
) {}
