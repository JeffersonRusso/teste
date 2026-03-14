package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_data_contract")
@Getter
@Setter
public class DataContractEntity {

    @Id
    @Column(name = "context_key")
    private String contextKey;

    @Column(name = "schema_definition", columnDefinition = "TEXT")
    private String schemaDefinition;

    private String description;
}
