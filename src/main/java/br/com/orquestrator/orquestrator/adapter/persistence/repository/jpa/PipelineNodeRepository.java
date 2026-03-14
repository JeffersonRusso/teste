package br.com.orquestrator.orquestrator.adapter.persistence.repository.jpa;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PipelineNodeRepository: Repositório JPA para nós do pipeline.
 * Visibilidade package-private para forçar o uso através do Adapter.
 */
interface PipelineNodeRepository extends JpaRepository<PipelineNodeEntity, UUID> {
    
    List<PipelineNodeEntity> findByPipelineId(UUID pipelineId);

    Optional<PipelineNodeEntity> findByName(String name);
}
