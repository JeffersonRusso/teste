package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_tag_rules")
@IdClass(TagRuleId.class)
public class TagRuleEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "tag_name")
    private String tagName;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "condition_expression")
    private String conditionExpression;

    private String description;

    @Column(name = "is_active")
    private boolean active;

    private Integer priority;
}
