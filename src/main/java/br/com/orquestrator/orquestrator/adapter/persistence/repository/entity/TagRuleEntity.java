package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_tag_rule")
@Getter
@Setter
public class TagRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "condition_expression")
    private String conditionExpression;

    private Integer priority;

    @Column(name = "is_active")
    private Boolean isActive;
}
