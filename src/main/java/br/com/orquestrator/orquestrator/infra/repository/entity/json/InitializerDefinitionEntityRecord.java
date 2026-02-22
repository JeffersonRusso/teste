package br.com.orquestrator.orquestrator.infra.repository.entity.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representação exata do JSON na tabela tb_initialization_plan.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InitializerDefinitionEntityRecord(String id, Integer version) {}