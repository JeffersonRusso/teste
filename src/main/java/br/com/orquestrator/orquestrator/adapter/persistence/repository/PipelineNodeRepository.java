package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PipelineNodeRepository extends JpaRepository<PipelineNodeEntity, UUID> {
    List<PipelineNodeEntity> findByPipelineId(UUID pipelineId);
}
