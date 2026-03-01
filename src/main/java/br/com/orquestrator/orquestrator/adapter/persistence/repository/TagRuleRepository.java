package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.TagRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRuleRepository extends JpaRepository<TagRuleEntity, Long> {
    
    @Query("SELECT t FROM TagRuleEntity t WHERE t.isActive = true ORDER BY t.priority DESC")
    List<TagRuleEntity> findAllActive();
}
