package br.com.orquestrator.orquestrator.adapter.persistence.repository.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataMappingEntityRecord(String name, String path, String type) {}