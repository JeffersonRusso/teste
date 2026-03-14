package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

/**
 * PipelineNodeEntity: Representação relacional completa de um nó do pipeline.
 * 
 * Mantendo o modelo relacional clássico para inputs e outputs.
 */
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

    private String name;
    private String type;

    @Column(name = "configuration", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode configuration;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "node_id")
    private List<PipelineNodeInputEntity> inputs;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "node_id")
    private List<PipelineNodeOutputEntity> outputs;
}
