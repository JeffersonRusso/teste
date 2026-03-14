/*
package br.com.orquestrator.orquestrator.tasks.registry;

import br.com.orquestrator.orquestrator.core.ports.output.TaskRepository;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

// CLASSE DESCONTINUADA: A busca de definições de tasks é feita diretamente via TaskRepository (JpaPipelineRepositoryAdapter).
// Esta classe é redundante e violava a arquitetura ao acessar repositórios package-private.
@Component
@RequiredArgsConstructor
public class DatabaseTaskDefinitionProvider implements TaskDefinitionProvider {

    private final TaskRepository repository;

    @Override
    public Optional<TaskDefinition> getDefinition(String name) {
        return repository.findByName(name);
    }
}
*/
