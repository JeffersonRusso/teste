package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * PipelineVersionEntity: Representação relacional da versão de um pipeline.
 */
@Entity
@Table(name = "tb_pipeline_version")
@Getter
@Setter
public class PipelineVersionEntity {

    @Id
    @Column(name = "pipeline_id")
    private UUID pipelineId;

    @Column(name = "operation_type")
    private String operationType;

    private Integer version;

    @Column(name = "timeout_ms")
    private Long timeoutMs;

    /**
     * requiredOutputs: Mapeado como JSON nativo.
     * O Hibernate cuida da conversão de/para Set<String> automaticamente.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_outputs")
    private Set<String> requiredOutputs;

    @Column(name = "execution_strategy")
    private String executionStrategy;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
