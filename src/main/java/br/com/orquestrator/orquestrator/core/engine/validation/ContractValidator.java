package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.core.engine.runtime.SignalSchema;
import br.com.orquestrator.orquestrator.domain.model.definition.PipelineDefinition;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class ContractValidator {

    public void validate(PipelineDefinition definition, SignalSchema schema) {
        Set<String> required = definition.defaultRequiredOutputs();
        if (required == null) return;

        for (String signalName : required) {
            if (!schema.canProvide(signalName)) {
                throw new PipelineException(String.format(
                    "Erro de Contrato: O pipeline para '%s' prometeu entregar '%s', mas esse dado não será produzido.",
                    definition.operationType(), signalName));
            }
        }
    }
}
