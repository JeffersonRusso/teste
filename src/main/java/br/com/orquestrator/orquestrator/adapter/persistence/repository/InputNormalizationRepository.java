package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InputNormalizationEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InputNormalizationRepository extends JpaRepository<InputNormalizationEntity, Long> {
    @Cacheable("inputNormalization")
    List<InputNormalizationEntity> findByOperationType(String operationType);
}