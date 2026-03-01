package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.domain.ContextKey;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.infra.util.PathNavigator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ExecutionContext: O Banco de Dados em Memória do Request.
 * Gerencia o estado com proteção de chaves e constraints de integridade.
 */
@Slf4j
@Getter
public final class ExecutionContext {

    private final String correlationId;
    private final String operationType;
    private final Map<String, Object> data = new ConcurrentHashMap<>(64);
    private final Set<String> tags = ConcurrentHashMap.newKeySet();
    
    // Módulo de Constraints (Lista de guardiões)
    private final List<Constraint> constraints = new CopyOnWriteArrayList<>();

    public ExecutionContext(String correlationId, String operationType, Map<String, Object> initialData) {
        this.correlationId = correlationId;
        this.operationType = operationType;
        this.tags.add("default");
        
        // Inicializa as chaves soberanas
        if (initialData != null) this.data.putAll(initialData);
        this.data.put(ContextKey.OPERATION_TYPE, operationType);

        // Adiciona Constraints Padrão (Soberania)
        addDefaultConstraints();
    }

    private void addDefaultConstraints() {
        // 1. Constraint de Somente-Leitura para chaves do sistema
        this.constraints.add((key, value, current) -> {
            if (current.containsKey(key) && (ContextKey.RAW.equals(key) || ContextKey.OPERATION_TYPE.equals(key))) {
                throw new PipelineException("Violação de Integridade: A chave '" + key + "' é protegida.");
            }
        });
    }

    /**
     * Adiciona uma nova regra de integridade ao contexto.
     */
    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    /**
     * PUT: Grava dados aplicando todas as constraints.
     */
    public void put(String key, Object value) {
        if (key == null || value == null) return;

        // Executa todas as constraints antes de gravar
        for (Constraint constraint : constraints) {
            constraint.validate(key, value, data);
        }

        this.data.put(key, value);
    }

    /**
     * GET: Recupera dados com Fast-Path e Navegação Inteligente.
     */
    public Object get(String key) {
        if (key == null) return null;
        Object value = this.data.get(key);
        if (value != null) return value;
        if (key.indexOf('.') != -1) return PathNavigator.find(this.data, key);
        return null;
    }

    public void addTag(String tag) { if (tag != null) this.tags.add(tag); }
    public Set<String> getTags() { return Collections.unmodifiableSet(tags); }
    public Map<String, Object> getRoot() { return data; }
}
