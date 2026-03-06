package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tb_pipeline_node_output")
@Getter
@Setter
public class PipelineNodeOutputEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id")
    private UUID nodeId;

    @Column(name = "local_key")
    private String localKey;

    @Column(name = "target_key")
    private String targetKey;

    @Column(name = "produced_semantic_type")
    private String producedSemanticType;
}
