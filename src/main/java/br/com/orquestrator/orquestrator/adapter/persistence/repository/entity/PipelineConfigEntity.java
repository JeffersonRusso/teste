package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_pipeline_config")
public class PipelineConfigEntity {

    @Id
    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "timeout_ms")
    private Long timeoutMs;

    private String description;
}
