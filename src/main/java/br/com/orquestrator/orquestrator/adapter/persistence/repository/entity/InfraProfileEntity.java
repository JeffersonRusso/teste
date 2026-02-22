package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_infra_profiles")
public class InfraProfileEntity {

    @Id
    @Column(name = "profile_id")
    private String profileId;

    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private List<ProfileFeatureEntity> features;
}
