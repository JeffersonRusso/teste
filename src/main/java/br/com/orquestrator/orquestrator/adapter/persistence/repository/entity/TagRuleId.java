package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TagRuleId implements Serializable {

    private String tagName;
    private String conditionExpression;
}
