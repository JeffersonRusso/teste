package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.FlowConfigEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.FlowConfigId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlowConfigRepository extends JpaRepository<FlowConfigEntity, FlowConfigId> {
    
    @Query("SELECT f FROM FlowConfigEntity f WHERE f.operationType = :operationType AND f.active = true ORDER BY f.version DESC LIMIT 1")
    Optional<FlowConfigEntity> findLatestActive(String operationType);
    
    @Query("SELECT f FROM FlowConfigEntity f WHERE f.operationType = :operationType AND f.version = :version AND f.active = true")
    Optional<FlowConfigEntity> findSpecificVersion(String operationType, Integer version);

    @Query("SELECT DISTINCT f.operationType FROM FlowConfigEntity f WHERE f.active = true")
    List<String> findAllActiveOperations();
}
