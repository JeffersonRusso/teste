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
@Table(name = "tb_feature_templates")
public class FeatureTemplateEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "template_id")
    private String templateId;

    @Column(name = "feature_type", nullable = false)
    private String featureType;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "config_json")
    private JsonNode config;

    @Column(name = "description")
    private String description;
}
