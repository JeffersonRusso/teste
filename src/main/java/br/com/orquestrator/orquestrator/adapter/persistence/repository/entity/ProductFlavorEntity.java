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
@Table(name = "tb_product_flavors")
public class ProductFlavorEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "flavor_id")
    private String flavorId;

    @Column(name = "description")
    private String description;

    @Column(name = "parameters")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode parameters;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
