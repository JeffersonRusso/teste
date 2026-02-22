package br.com.orquestrator.orquestrator.infra.repository;

import br.com.orquestrator.orquestrator.infra.repository.entity.OperationParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationParamRepository extends JpaRepository<OperationParamEntity, String> {
}