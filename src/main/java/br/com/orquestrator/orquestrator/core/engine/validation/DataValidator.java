package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.domain.model.DataType;
import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.exception.PipelineValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DataValidator: Orquestrador de estratégias de validação.
 * SOLID: Open/Closed Principle e Strategy Pattern.
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
        var value = DataValue.of(rawValue);

        // 1. Validação de Obrigatoriedade
        if (value instanceof DataValue.Empty) {
            if (contract.required()) {
                throw new PipelineValidationException("O dado '" + contract.contextKey() + "' é obrigatório.");
            }
            return;
        }

        // 2. Seleção de Estratégia
        ValidationStrategy strategy = strategies.get(contract.type());
        
        // Fallback para Scalar se não houver estratégia específica (ex: STRING, NUMBER, BOOLEAN)
        if (strategy == null && contract.type() != DataType.OBJECT) {
            strategy = strategies.get(DataType.STRING); // ScalarStrategy
        }

        if (strategy != null) {
            strategy.validate(compiled, value);
        }
    }
}
