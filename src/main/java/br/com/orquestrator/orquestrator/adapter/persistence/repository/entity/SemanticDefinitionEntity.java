package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * SemanticDefinitionEntity: Representação relacional da definição semântica.
 * Mapeada para a tabela tb_semantic_definition.
 */
@Entity
@Table(name = "tb_semantic_definition")
@Getter
@Setter
public class SemanticDefinitionEntity {

    @Id
    @Column(name = "type_name")
    private String typeName;

    private String description;

    @Column(name = "format_script", columnDefinition = "TEXT")
    private String formatScript;

    @Column(name = "validation_script", columnDefinition = "TEXT")
    private String validationScript;
}
