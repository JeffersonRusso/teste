package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

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

    @Column(name = "semantic_type")
    private String semanticType;

    @Column(name = "format_rule")
    private String formatRule;

    @Column(name = "schema_definition", columnDefinition = "TEXT")
    private String schemaDefinition;

    @Column(name = "min_value")
    private Double minValue;

    @Column(name = "max_value")
    private Double maxValue;

    @Column(name = "is_required")
    private Boolean isRequired;

    private String description;
}
