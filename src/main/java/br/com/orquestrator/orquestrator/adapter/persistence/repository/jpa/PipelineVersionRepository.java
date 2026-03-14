package br.com.orquestrator.orquestrator.adapter.persistence.repository.jpa;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * PipelineVersionRepository: Repositório JPA para versões do pipeline.
 * Visibilidade package-private para forçar o uso através do Adapter.
 */
interface PipelineVersionRepository extends JpaRepository<PipelineVersionEntity, UUID> {
    
    @Query("SELECT p FROM PipelineVersionEntity p WHERE p.operationType = :operationType AND p.isActive = true")
    Optional<PipelineVersionEntity> findActive(String operationType);

    @Query("SELECT DISTINCT p.operationType FROM PipelineVersionEntity p WHERE p.isActive = true")
    Set<String> findAllActiveOperationTypes();
}
