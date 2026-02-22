package br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineConfigRepository;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.PipelineConfigEntity;
import br.com.orquestrator.orquestrator.domain.model.PipelineConfig;
import br.com.orquestrator.orquestrator.domain.repository.PipelineConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPipelineConfigAdapter implements PipelineConfigProvider {

    private final PipelineConfigRepository repository;

    @Override
    public Optional<PipelineConfig> getConfig(String operationType) {
        return repository.findById(operationType)
                .map(entity -> new PipelineConfig(
                        Duration.ofMillis(entity.getTimeoutMs()),
                        entity.getDescription()
                ));
    }
}
