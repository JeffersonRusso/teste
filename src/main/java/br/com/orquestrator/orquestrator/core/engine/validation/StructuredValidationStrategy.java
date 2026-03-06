package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.model.DataType;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.exception.PipelineValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class StructuredValidationStrategy implements ValidationStrategy {

    private final ObjectMapper objectMapper;

    @Override public DataType getType() { return DataType.OBJECT; }

    @Override
    public void validate(ContractRegistry.CompiledContract compiled, DataValue value) {
        if (compiled.schema() == null) return;

        try {
            JsonNode node = objectMapper.valueToTree(value.raw());
            Set<ValidationMessage> errors = compiled.schema().validate(node);

            if (!errors.isEmpty()) {
                throw new PipelineValidationException(String.format(
                    "Contrato violado em '%s': %s", 
                    compiled.definition().contextKey(), errors.iterator().next().getMessage()));
            }
        } catch (Exception e) {
            if (e instanceof PipelineValidationException) throw (PipelineValidationException) e;
            throw new PipelineValidationException("Falha ao validar esquema do objeto: " + compiled.definition().contextKey(), e);
        }
    }
}
