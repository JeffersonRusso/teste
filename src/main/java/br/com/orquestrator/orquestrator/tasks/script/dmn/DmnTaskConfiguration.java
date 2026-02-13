package br.com.orquestrator.orquestrator.tasks.script.dmn;

import org.camunda.bpm.dmn.engine.DmnDecision;
import java.util.Map;

/**
 * Configuração imutável e tipada para a DmnTask.
 * Contém a decisão já parseada e os mapeamentos necessários.
 */
public record DmnTaskConfiguration(
    String decisionKey,
    String dmnFile,
    DmnDecision decision,
    Map<String, Object> inputMapping
) {}
