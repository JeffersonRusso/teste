package br.com.orquestrator.orquestrator.adapter.persistence.repository.converter;

import br.com.orquestrator.orquestrator.domain.model.FeaturePhases;
import jakarta.persistence.Converter;

@Converter
public class FeaturePhasesConverter extends AbstractJsonConverter<FeaturePhases> {
    public FeaturePhasesConverter() {
        super(FeaturePhases.class);
    }

    @Override
    protected FeaturePhases defaultEmptyObject() {
        return new FeaturePhases(null, null, null);
    }
}
