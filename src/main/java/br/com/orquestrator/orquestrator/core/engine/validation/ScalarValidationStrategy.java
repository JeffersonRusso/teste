package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.model.DataType;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.exception.PipelineValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ScalarValidationStrategy implements ValidationStrategy {

    // Esta estratégia atende múltiplos tipos escalares
    @Override public DataType getType() { return DataType.STRING; } // Fallback

    @Override
    public void validate(ContractRegistry.CompiledContract compiled, DataValue value) {
        var contract = compiled.definition();
        Object raw = value.raw();

        if (contract.formatRule() != null && raw instanceof String str) {
            if (!Pattern.matches(contract.formatRule(), str)) {
                throw new PipelineValidationException("Formato inválido para '" + contract.contextKey() + "'");
            }
        }

        if (contract.minValue() != null && raw instanceof Number num) {
            if (num.doubleValue() < contract.minValue()) {
                throw new PipelineValidationException("Valor abaixo do mínimo para '" + contract.contextKey() + "'");
            }
        }

        if (contract.maxValue() != null && raw instanceof Number num) {
            if (num.doubleValue() > contract.maxValue()) {
                throw new PipelineValidationException("Valor acima do máximo para '" + contract.contextKey() + "'");
            }
        }
    }
}
