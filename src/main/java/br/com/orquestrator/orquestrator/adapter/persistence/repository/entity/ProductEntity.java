package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.converter.ListStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_products")
public class ProductEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "product_id")
    private String productId;

    @Column(name = "description")
    private String description;

    @Column(name = "required_output")
    private String requiredOutput;

    @Column(name = "sla_timeout_ms")
    private Integer slaTimeoutMs;

    @Column(name = "base_tags")
    @Convert(converter = ListStringConverter.class)
    private List<String> baseTags;
}
