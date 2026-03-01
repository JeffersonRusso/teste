package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.json.TaskConfig;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

    @Column(name = "inputs")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> inputs;

    @Column(name = "outputs")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> outputs;
    
    @Column(name = "activation_tags")
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> activationTags;
    
    @Column(name = "guard_condition")
    private String guardCondition;
    
    @Column(name = "fail_fast")
    private Boolean failFast;
}
