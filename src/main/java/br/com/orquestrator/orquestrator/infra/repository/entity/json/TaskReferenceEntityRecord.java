package br.com.orquestrator.orquestrator.infra.repository.entity.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representação do JSON de referência de task na tabela tb_flow_config.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskReferenceEntityRecord(String id, Integer version) {}