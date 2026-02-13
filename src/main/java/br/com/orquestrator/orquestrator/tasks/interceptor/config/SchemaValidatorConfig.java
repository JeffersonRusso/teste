package br.com.orquestrator.orquestrator.tasks.interceptor.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SchemaValidatorConfig(
    boolean failOnInvalid,
    int sampleRate // 0 a 100. Default 0 (valida tudo? ou nada? Vamos assumir 100 se nulo/0 para segurança, ou 100 default)
) {
    public SchemaValidatorConfig {
        if (sampleRate <= 0) sampleRate = 100; // Default: Valida 100%
    }
}
