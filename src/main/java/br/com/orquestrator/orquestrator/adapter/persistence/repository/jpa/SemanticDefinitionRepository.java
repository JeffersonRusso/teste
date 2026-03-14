package br.com.orquestrator.orquestrator.adapter.persistence.repository.jpa;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.SemanticDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SemanticDefinitionRepository: Repositório JPA para as definições semânticas.
 * Visibilidade package-private.
 */
interface SemanticDefinitionRepository extends JpaRepository<SemanticDefinitionEntity, String> {
}
