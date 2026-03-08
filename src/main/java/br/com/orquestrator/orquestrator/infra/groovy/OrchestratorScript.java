package br.com.orquestrator.orquestrator.infra.groovy;

import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * OrchestratorScript: Classe base para scripts Groovy no orquestrador.
 * Agora desacoplada do ContextHolder e focada no Shadow Context.
 */
@Slf4j
public abstract class OrchestratorScript extends Script {

    /** Retorna o valor bruto (raw) de um input mapeado para o nó. */
    @SuppressWarnings("unchecked")
    public Object get(String key) {
        Map<String, Object> inputs = (Map<String, Object>) getBinding().getVariable("inputs");
        return inputs != null ? inputs.get(key) : null;
    }

    /** Atalho para log. */
    public void log(String message) {
        log.info("[Groovy] Node {}: {}", getBinding().getVariable("nodeId"), message);
    }

    /** 
     * Nota: O método 'put' foi removido pois scripts agora são funções puras.
     * O resultado do script deve ser o valor de retorno da última linha.
     */
}
