package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "tb_profile_features")
public class ProfileFeatureEntity {

    @EmbeddedId
    private ProfileFeatureId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("templateId")
    @JoinColumn(name = "template_id")
    private FeatureTemplateEntity template;

    public String getPhase() {
        return id != null ? id.getPhase() : null;
    }

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class ProfileFeatureId implements Serializable {
        @Column(name = "profile_id")
        private String profileId;
        @Column(name = "template_id")
        private String templateId;
        @Column(name = "phase")
        private String phase;
    }
}
