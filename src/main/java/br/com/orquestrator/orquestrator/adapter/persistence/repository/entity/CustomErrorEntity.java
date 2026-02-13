package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_error_catalog")
public class CustomErrorEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "message_template", nullable = false)
    private String messageTemplate;
}
