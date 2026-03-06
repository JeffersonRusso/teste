package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import br.com.orquestrator.orquestrator.domain.model.DataType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_data_contract")
@Getter
@Setter
public class DataContractEntity {

    @Id
    @Column(name = "context_key")
    private String contextKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type")
    private DataType dataType;

    @Column(name = "semantic_type")
    private String semanticType;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "format_rule")
    private String formatRule;

    @Column(name = "schema_definition")
    private String schemaDefinition;

    @Column(name = "min_value")
    private Double minValue;

    @Column(name = "max_value")
    private Double maxValue;

    private String description;
}
