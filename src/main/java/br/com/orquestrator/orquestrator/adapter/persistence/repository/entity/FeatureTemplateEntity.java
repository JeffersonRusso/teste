package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "tb_feature_templates")
public class FeatureTemplateEntity {

    @Id
    @Column(name = "template_id")
    private String templateId;

    @Column(name = "feature_type")
    private String featureType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_json")
    private Map<String, Object> config;

    private String description;
}
