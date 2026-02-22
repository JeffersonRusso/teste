package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_initialization_plan")
public class InitializationPlanEntity {

    @Id
    @Column(name = "operation_type")
    private String operationType;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "operation_type")
    private List<InitializationTaskEntity> tasks;
}
