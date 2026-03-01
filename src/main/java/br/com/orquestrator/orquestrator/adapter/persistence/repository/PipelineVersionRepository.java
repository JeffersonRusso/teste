package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PipelineVersionRepository extends JpaRepository<PipelineVersionEntity, UUID> {

    @Query("SELECT p FROM PipelineVersionEntity p WHERE p.operationType = :operationType AND p.isActive = true")
    Optional<PipelineVersionEntity> findActive(String operationType);

    @Query("SELECT DISTINCT p.operationType FROM PipelineVersionEntity p WHERE p.isActive = true")
    List<String> findAllActiveOperations();
}
