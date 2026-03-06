package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.PipelineNodeRepository;
import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseTaskDefinitionProvider implements TaskDefinitionProvider {

    private final PipelineNodeRepository repository;

    @Override
    public Optional<TaskDefinition> getDefinition(String name) {
        // Busca no banco de nós por nome (ou template)
        return repository.findByName(name)
                .map(entity -> entity.toDomain());
    }
}
