package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.DataContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataContractRepository extends JpaRepository<DataContractEntity, String> {
}
