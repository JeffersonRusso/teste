package br.com.orquestrator.orquestrator.tasks.script.dmn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.camunda.bpm.dmn.engine.DmnDecision;
import java.util.Map;

/**
 * Configuração imutável e tipada para a DmnTask.
 */
public record DmnTaskConfiguration(
    @JsonProperty("decisionKey") String decisionKey,
    @JsonProperty("dmnFile") String dmnFile,
    @JsonIgnore DmnDecision decision,
    @JsonProperty("inputs") Map<String, Object> inputMapping
) {}
