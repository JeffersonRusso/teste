package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "tb_decorator_template")
@Getter
@Setter
public class DecoratorTemplateEntity {

    @Id
    @Column(name = "template_id")
    private String templateId;

    private String type;
    private String phase;

    @Column(name = "default_configuration")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> defaultConfiguration;

    private String description;
}
