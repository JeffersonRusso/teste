package br.com.orquestrator.orquestrator.adapter.persistence.repository.jpa;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.adapter.PipelineMapper;
import br.com.orquestrator.orquestrator.domain.exception.RepositoryException;
import br.com.orquestrator.orquestrator.core.ports.output.PipelineRepository;
import br.com.orquestrator.orquestrator.core.ports.output.TaskRepository;
import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.domain.model.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * JpaPipelineRepositoryAdapter: Orquestra a persistência JPA para Pipelines e Tasks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPipelineRepositoryAdapter implements PipelineRepository, TaskRepository {

    private final PipelineVersionRepository versionRepository;
    private final PipelineNodeRepository nodeRepository;
    private final PipelineMapper pipelineMapper;

    @Override
    @Cacheable(value = "pipeline_definitions", key = "#operationType")
    public Optional<PipelineDefinition> findActive(String operationType) {
        try {
            log.info("Buscando definição de pipeline ativa: {}", operationType);
            
            return versionRepository.findActive(operationType)
                    .map(version -> {
                        var tasks = fetchTasks(version.getPipelineId());
                        return pipelineMapper.toDefinition(version, tasks);
                    });
        } catch (DataAccessException e) {
            log.error("Erro de banco ao buscar pipeline: {}", operationType, e);
            throw new RepositoryException("Falha na persistência ao recuperar pipeline: " + operationType, e);
        }
    }

    @Override
    public Set<String> findAllActiveOperationTypes() {
        try {
            return versionRepository.findAllActiveOperationTypes();
        } catch (DataAccessException e) {
            throw new RepositoryException("Erro ao buscar tipos de operação ativos", e);
        }
    }

    @Override
    @Cacheable(value = "task_definitions", key = "#name")
    public Optional<TaskDefinition> findByName(String name) {
        try {
            return nodeRepository.findByName(name)
                    .map(pipelineMapper::toDomain);
        } catch (DataAccessException e) {
            throw new RepositoryException("Erro ao buscar task por nome: " + name, e);
        }
    }

    @Override
    public List<TaskDefinition> findAll() {
        try {
            return nodeRepository.findAll().stream()
                    .map(pipelineMapper::toDomain)
                    .toList();
        } catch (DataAccessException e) {
            throw new RepositoryException("Erro ao buscar todas as tasks", e);
        }
    }

    private List<TaskDefinition> fetchTasks(UUID pipelineId) {
        try {
            return nodeRepository.findByPipelineId(pipelineId).stream()
                    .map(pipelineMapper::toDomain)
                    .toList();
        } catch (DataAccessException e) {
            throw new RepositoryException("Erro ao buscar tasks para o pipeline: " + pipelineId, e);
        }
    }
}
