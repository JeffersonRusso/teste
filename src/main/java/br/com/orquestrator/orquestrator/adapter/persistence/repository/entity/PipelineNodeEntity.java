package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.json.TaskConfig;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.NodeId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_pipeline_node")
@Getter
@Setter
public class PipelineNodeEntity {

    @Id
    @Column(name = "node_id")
    private UUID nodeId;

    @Column(name = "pipeline_id")
    private UUID pipelineId;

    @Column(name = "template_id")
    private String templateId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private TaskTemplateEntity template;

    private String name;
    private String type;

    @Column(name = "configuration")
    @JdbcTypeCode(SqlTypes.JSON)
    private TaskConfig configuration;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "node_id")
    private List<PipelineNodeInputEntity> inputs;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "node_id")
    private List<PipelineNodeOutputEntity> outputs;
    
    @Column(name = "activation_tags")
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> activationTags;
    
    @Column(name = "guard_condition")
    private String guardCondition;
    
    @Column(name = "fail_fast")
    private Boolean failFast;

    public TaskDefinition toDomain() {
        String finalType = type != null ? type : (template != null ? template.getType() : "UNKNOWN");
        Map<String, Object> finalConfig = configuration != null ? configuration.toFullMap() : Map.of();
        
        Map<String, String> inputMap = inputs != null ? inputs.stream()
                .collect(Collectors.toMap(PipelineNodeInputEntity::getLocalKey, PipelineNodeInputEntity::getSourcePath)) : Map.of();

        Map<String, String> outputMap = outputs != null ? outputs.stream()
                .collect(Collectors.toMap(PipelineNodeOutputEntity::getLocalKey, PipelineNodeOutputEntity::getTargetKey)) : Map.of();

        return new TaskDefinition(
            new NodeId(nodeId.toString()), 1, name, finalType, 
            0L, finalConfig, List.of(), 
            failFast != null ? failFast : true,
            inputMap, outputMap, activationTags, guardCondition,
            configuration != null && Boolean.TRUE.equals(configuration.getGlobal()),
            0L
        );
    }
}
