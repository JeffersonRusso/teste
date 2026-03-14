package br.com.orquestrator.orquestrator.adapter.persistence.repository.jpa;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.DataContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DataContractRepository: Repositório JPA para contratos de dados.
 * Visibilidade package-private para forçar o uso através do Adapter.
 */
interface DataContractRepository extends JpaRepository<DataContractEntity, String> {
}
