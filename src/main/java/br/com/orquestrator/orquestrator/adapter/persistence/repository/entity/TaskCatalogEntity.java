package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import br.com.orquestrator.orquestrator.infra.repository.entity.json.DataMappingEntityRecord;
import br.com.orquestrator.orquestrator.infra.repository.entity.json.FeaturePhasesEntityRecord;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "tb_task_catalog")
@IdClass(TaskCatalogId.class)
public class TaskCatalogEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "task_id")
    private String taskId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "version") // Mapeia para a coluna 'version' no banco
    private Integer taskVersion = 1;

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
    @JdbcTypeCode(SqlTypes.JSON)
    private List<DataMappingEntityRecord> requires;

    @Column(name = "produces_json")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<DataMappingEntityRecord> produces;

    @Column(name = "config_json")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode config;

    @Column(name = "features_json")
    @JdbcTypeCode(SqlTypes.JSON)
    private FeaturePhasesEntityRecord features;
    
    @Column(name = "response_schema")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode responseSchema;
    
    @Column(name = "infra_profile_id")
    private String infraProfileId;

    @Column(name = "is_active")
    private boolean active;
}
