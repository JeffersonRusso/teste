package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_input_normalization")
@Getter
@Setter
public class InputNormalizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "target_field")
    private String targetField;

    @Column(name = "source_expression")
    private String sourceExpression;

    @Column(name = "transformation_expression")
    private String transformationExpression;
}
