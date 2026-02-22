package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_flow_config")
@IdClass(FlowConfigId.class)
public class FlowConfigEntity {

    @Id
    @Column(name = "operation_type")
    private String operationType;

    @Id
    private Integer version;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_outputs")
    private List<String> requiredOutputs;

    @OneToMany(mappedBy = "flow", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<FlowTaskEntity> tasks;

    private String description;

    @Column(name = "is_active")
    private boolean active;

}
