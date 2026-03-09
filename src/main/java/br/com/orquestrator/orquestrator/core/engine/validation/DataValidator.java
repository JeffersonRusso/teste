package br.com.orquestrator.orquestrator.core.engine.validation;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * DataValidator: Valida um JsonNode contra um contrato de dados.
 * Por enquanto, apenas verifica a obrigatoriedade.
 */
@Component
@RequiredArgsConstructor
public class DataValidator {

    public void validate(ContractRegistry.CompiledContract compiled, JsonNode value) {
        if (compiled == null) return;

        if (compiled.isRequired() && (value == null || value.isMissingNode() || value.isNull())) {
            throw new IllegalArgumentException("Dado obrigatório não encontrado: " + compiled.key());
        }
        
        // Futuramente, podemos adicionar validação de schema JSON aqui.
    }
}
