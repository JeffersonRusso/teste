package br.com.orquestrator.orquestrator.domain.model;

import java.time.Duration;

/**
 * Configurações globais de execução da pipeline.
 */
public record PipelineConfig(
    Duration timeout,
    String description
) {}