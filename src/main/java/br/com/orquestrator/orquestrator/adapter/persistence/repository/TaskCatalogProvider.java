package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;

import java.util.List;

/**
 * Porta de saída para acesso ao catálogo de tasks.
 * Desacopla o domínio da tecnologia de persistência (JPA, YAML, etc).
 */
public interface TaskCatalogProvider {
    List<TaskDefinition> findAllActive();
}
