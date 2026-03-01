package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tb_pipeline_node_decorator")
@Getter
@Setter
@IdClass(PipelineNodeDecoratorId.class)
public class PipelineNodeDecoratorEntity {

    @Id
    @Column(name = "node_id")
    private UUID nodeId;

    @Id
    @Column(name = "template_id")
    private String templateId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private DecoratorTemplateEntity template;

    @Column(name = "override_configuration")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> overrideConfiguration;

    @Column(name = "execution_order")
    private Integer executionOrder;
}
