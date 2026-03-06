package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InputNormalizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InputNormalizationRepository extends JpaRepository<InputNormalizationEntity, Long> {
    List<InputNormalizationEntity> findByOperationType(String operationType);
}
