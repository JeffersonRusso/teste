package br.com.orquestrator.orquestrator.infra.repository.entity.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataMappingEntityRecord(String name, String path, String type) {}