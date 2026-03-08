package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.model.DataType;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.DataValueFactory;
import br.com.orquestrator.orquestrator.exception.PipelineValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DataValidator: Orquestrador de estratégias de validação.
 */
@Component
public class DataValidator {

    private final Map<DataType, ValidationStrategy> strategies;

    public DataValidator(List<ValidationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ValidationStrategy::getType, Function.identity(), (a, b) -> a));
    }

    public void validate(ContractRegistry.CompiledContract compiled, Object rawValue) {
        var contract = compiled.definition();
        var value = DataValueFactory.of(rawValue);

        // 1. Validação de Obrigatoriedade
        if (value.isEmpty()) {
            if (contract.required()) {
                throw new PipelineValidationException("O dado '" + contract.contextKey() + "' é obrigatório.");
            }
            return;
        }

        // 2. Seleção de Estratégia
        ValidationStrategy strategy = strategies.get(contract.type());
        
        if (strategy == null && contract.type() != DataType.OBJECT) {
            strategy = strategies.get(DataType.STRING);
        }

        if (strategy != null) {
            strategy.validate(compiled, value);
        }
    }
}
