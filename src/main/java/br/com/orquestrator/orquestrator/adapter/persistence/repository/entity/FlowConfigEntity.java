package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.converter.JsonNodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_flow_config")
@IdClass(FlowConfigId.class)
public class FlowConfigEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "operation_type")
    private String operationType;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "required_outputs", columnDefinition = "TEXT")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode requiredOutputs;

    @Column(name = "allowed_tasks", columnDefinition = "TEXT")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode allowedTasks;

    @Column(name = "description")
    private String description;
    
    @Column(name = "is_active")
    private boolean active = true;
}
