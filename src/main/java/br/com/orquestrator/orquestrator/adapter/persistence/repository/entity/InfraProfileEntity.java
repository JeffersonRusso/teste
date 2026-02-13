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
@Table(name = "tb_infra_profiles")
public class InfraProfileEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "profile_id")
    private String profileId;

    @Column(name = "description")
    private String description;

    @Column(name = "default_controls")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode defaultControls;
}
