package br.com.orquestrator.orquestrator.domain.model;

/**
 * Representa um mapeamento de dados (entrada ou saída) de uma task.
 */
public record DataMapping(String name, String path, String type) {}