/*
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
public class PipelineNodeDecoratorEntity {

    @EmbeddedId
    private PipelineNodeDecoratorId id;

    @Column(name = "node_id", insertable = false, updatable = false)
    private UUID nodeId;

    @Column(name = "execution_order")
    private Integer executionOrder;

    @Column(name = "override_configuration")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> overrideConfiguration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id")
    private DecoratorTemplateEntity template;
}
*/
