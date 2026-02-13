package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_input_normalization")
public class InputNormalizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
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
