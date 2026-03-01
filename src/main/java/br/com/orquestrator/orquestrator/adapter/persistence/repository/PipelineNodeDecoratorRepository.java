package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeDecoratorEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineNodeDecoratorId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PipelineNodeDecoratorRepository extends JpaRepository<PipelineNodeDecoratorEntity, PipelineNodeDecoratorId> {
    List<PipelineNodeDecoratorEntity> findByNodeIdOrderByExecutionOrderAsc(UUID nodeId);
}
