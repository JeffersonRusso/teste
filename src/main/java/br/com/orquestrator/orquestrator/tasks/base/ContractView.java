package br.com.orquestrator.orquestrator.tasks.base;

import br.com.orquestrator.orquestrator.domain.model.TaskDefinition;
import br.com.orquestrator.orquestrator.domain.vo.DataValue;
import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação de TaskData que enforça o contrato e suporta navegação em caminhos.
 * Atua como um Sandbox (Sandbox Pattern), garantindo o Princípio do Menor Privilégio.
 * Java 21: Refatorado para maior performance via delegação especializada e contrato pré-calculado.
 */
@Slf4j
public class ContractView extends AbstractMap<String, Object> implements TaskData {

    private final ExecutionContext context;
    private final String nodeId;
    private final Set<String> allowedInputs;
    private final Set<String> allowedOutputs;
    
    // Cache para o entrySet para evitar reconstrução constante durante a execução
    private Set<Entry<String, Object>> cachedEntrySet;

    public ContractView(ExecutionContext context, TaskDefinition definition) {
        this.context = context;
        this.nodeId = definition.getNodeId().value();
        
        // PERFORMANCE: O TaskDefinition agora já entrega o contrato pré-calculado no Warmup
        var contract = definition.getContract(); 
        this.allowedInputs = contract.allowedInputs();
        this.allowedOutputs = contract.allowedOutputs();
    }

    @Override
    public DataValue get(String key) {
        if (!allowedInputs.contains(key)) {
            log.trace(STR."Acesso negado: campo '\{key}' fora do contrato da task \{nodeId}");
            return DataValue.of(null);
        }

        // DELEGAÇÃO: PathResolver cuida da complexidade de navegação em Maps/JSON
        return PathResolver.resolve(context, key);
    }

    @Override
    public Object put(String key, Object value) {
        if (!allowedOutputs.contains(key)) {
            // Decisão Sênior: Se o dado não é esperado, a pipeline está corrompida ou a task violou o contrato
            throw new SecurityException(STR."Task [\{nodeId}] tentou gravar '\{key}', mas só tem permissão para: \{allowedOutputs}");
        }
        context.put(key, value);
        return value;
    }

    @Override
    public boolean has(String key) {
        return allowedInputs.contains(key) && get(key).isPresent();
    }

    @Override
    public void addMetadata(String key, Object value) {
        // FACADE: Acesso direto via contexto para rastro de alta performance
        context.trackTaskAction(nodeId, key, value);
    }

    @Override
    public Object getMetadata(String key) {
        // Busca metadados diretamente do tracker via Facade
        return context.getTracker().getSpan(nodeId)
                .map(span -> span.toMetrics().metadata().get(key))
                .orElse(null);
    }

    @Override
    public Map<String, Object> asMap() {
        return this; // A própria ContractView já estende AbstractMap
    }

    // --- Implementação de Map para a própria ContractView (para compatibilidade com engines externas) ---

    @Override
    public Object get(Object key) {
        return (key instanceof String s) ? get(s).unwrap() : null;
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String s) && allowedInputs.contains(s) && get(s).isPresent();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        if (cachedEntrySet == null) {
            this.cachedEntrySet = allowedInputs.stream()
                    .map(k -> {
                        Object val = get(k).unwrap();
                        return val != null ? new AbstractMap.SimpleImmutableEntry<>(k, val) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return cachedEntrySet;
    }

    @Override
    public int size() { return entrySet().size(); }
}
