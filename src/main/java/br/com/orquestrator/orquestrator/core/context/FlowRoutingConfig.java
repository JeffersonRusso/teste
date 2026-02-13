package br.com.orquestrator.orquestrator.core.context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa a configuração de roteamento de um fluxo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FlowRoutingConfig(
    @JsonProperty("default_version") Integer defaultVersion,
    @JsonProperty("canary") CanaryConfig canary
) {
    public int getDefaultVersion() {
        return defaultVersion != null ? defaultVersion : 1;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CanaryConfig(
        Integer version,
        Integer percentage
    ) {
        public int getVersion() {
            return version != null ? version : 1;
        }

        public int getPercentage() {
            return percentage != null ? percentage : 0;
        }
    }
}
