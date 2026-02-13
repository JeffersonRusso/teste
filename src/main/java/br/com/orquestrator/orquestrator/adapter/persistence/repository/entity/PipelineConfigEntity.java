package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_pipeline_config")
public class PipelineConfigEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "timeout_ms")
    private Long timeoutMs;

    @Column(name = "description")
    private String description;
}
