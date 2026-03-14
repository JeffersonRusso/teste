package br.com.orquestrator.orquestrator.adapter.persistence.repository.jpa;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TagRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * TagRuleRepository: Repositório JPA para regras de tags.
 * Visibilidade package-private para forçar o uso através do Adapter.
 */
interface TagRuleRepository extends JpaRepository<TagRuleEntity, Long> {
    
    @Query("SELECT t FROM TagRuleEntity t WHERE t.isActive = true ORDER BY t.priority DESC")
    List<TagRuleEntity> findAllActive();
}
