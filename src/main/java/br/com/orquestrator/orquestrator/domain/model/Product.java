package br.com.orquestrator.orquestrator.domain.model;

import java.util.Map;

/**
 * Modelo de domínio puro para um Produto.
 */
public record Product(
    String id,
    String name,
    Map<String, Object> config
) {}