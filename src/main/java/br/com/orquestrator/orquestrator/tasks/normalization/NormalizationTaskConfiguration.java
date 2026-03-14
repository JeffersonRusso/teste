package br.com.orquestrator.orquestrator.tasks.normalization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NormalizationTaskConfiguration(
    Map<String, String> rules
) {}
