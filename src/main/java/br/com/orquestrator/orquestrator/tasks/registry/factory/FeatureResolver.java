package br.com.orquestrator.orquestrator.tasks.registry.factory;

import br.com.orquestrator.orquestrator.domain.FeatureDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.FeatureTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeatureResolver {

    private final FeatureTemplateRepository templateRepository;

    public FeatureDefinition resolve(FeatureDefinition feat) {
        if (feat.templateRef() == null || feat.templateRef().isBlank()) {
            return feat;
        }
        return getTemplateFromDb(feat.templateRef());
    }

    @Cacheable("feature_templates")
    public FeatureDefinition getTemplateFromDb(String templateId) {
        return templateRepository.findById(templateId)
                .map(t -> new FeatureDefinition(t.getFeatureType(), null, t.getConfig()))
                .orElseThrow(() -> new PipelineException("Template de Feature não encontrado: " + templateId));
    }
}
