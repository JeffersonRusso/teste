package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tb_pipeline_node_input")
@Getter
@Setter
public class PipelineNodeInputEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id")
    private UUID nodeId;

    @Column(name = "local_key")
    private String localKey;

    @Column(name = "source_path")
    private String sourcePath;

    @Column(name = "expected_semantic_type")
    private String expectedSemanticType;

    @Column(name = "is_required")
    private Boolean isRequired;
}
