package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.converter.FeaturePhasesConverter;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.converter.JsonNodeConverter;
import br.com.orquestrator.orquestrator.domain.model.FeaturePhases;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_task_catalog")
@IdClass(TaskCatalogId.class)
public class TaskCatalogEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "task_id")
    private String taskId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "task_group")
    private String taskGroup;

    @Column(name = "description")
    private String description;

    @Column(name = "selector_expression", columnDefinition = "TEXT")
    private String selectorExpression;
    
    @Column(name = "criticality")
    private Integer criticality = 100;

    @Column(name = "requires_json")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode requires;

    @Column(name = "produces_json")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode produces;

    @Column(name = "config_json")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode config;

    @Column(name = "features_json")
    @Convert(converter = FeaturePhasesConverter.class)
    private FeaturePhases features;
    
    @Column(name = "response_schema")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode responseSchema;
    
    @Column(name = "infra_profile_id")
    private String infraProfileId;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
