package br.com.orquestrator.orquestrator.tasks.triplification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuração para a tarefa de Triplificação.
 * Define o namespace base para a criação dos recursos RDF.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TriplificationTaskConfiguration(
    @JsonProperty("base_uri") String baseUri
) {}
