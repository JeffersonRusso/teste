package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TaskCatalogEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TaskCatalogId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCatalogRepository extends JpaRepository<TaskCatalogEntity, TaskCatalogId> {

    // Método otimizado para o Assembler carregar tudo
    @Cacheable("tasks")
    @Query("SELECT t FROM TaskCatalogEntity t WHERE t.active = true")
    List<TaskCatalogEntity> findAllActive();
}
