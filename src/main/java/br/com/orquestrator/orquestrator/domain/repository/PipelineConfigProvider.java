package br.com.orquestrator.orquestrator.domain.repository;

import br.com.orquestrator.orquestrator.domain.model.PipelineConfig;
import java.util.Optional;

public interface PipelineConfigProvider {
    Optional<PipelineConfig> getConfig(String operationType);
}