package br.com.orquestrator.orquestrator.core.engine.validation;

import br.com.orquestrator.orquestrator.exception.PipelineValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * DataValidator: Especialista em validação híbrida (Bean Validation + JSON Schema).
 */
@Component
@RequiredArgsConstructor
public class DataValidator {

    private final ObjectMapper objectMapper;
    private final Validator beanValidator; // Injetado pelo Spring (Hibernate Validator)

    public void validate(ContractRegistry.CompiledContract compiled, Object value) {
        var def = compiled.definition();
        
        if (value == null) {
            if (def.required()) throw new PipelineValidationException("O dado '" + def.contextKey() + "' é obrigatório.");
            return;
        }

        // 1. Validação via JSON Schema (Para dados dinâmicos do banco)
        if (compiled.schema() != null) {
            validateWithSchema(compiled, value);
        } 
        
        // 2. Validação via Bean Validation (Se o objeto for um POJO/Record com anotações)
        validateBean(value);

        // 3. Validações Simples (Fallback para tipos primitivos)
        validateSimple(def, value);
    }

    private void validateWithSchema(ContractRegistry.CompiledContract compiled, Object value) {
        JsonNode node = objectMapper.valueToTree(value);
        Set<ValidationMessage> errors = compiled.schema().validate(node);

        if (!errors.isEmpty()) {
            throw new PipelineValidationException(String.format(
                "Contrato violado em '%s': %s", 
                compiled.definition().contextKey(), errors.iterator().next().getMessage()));
        }
    }

    private void validateBean(Object value) {
        // Valida anotações como @NotNull, @Email, @Min, etc.
        Set<ConstraintViolation<Object>> violations = beanValidator.validate(value);
        if (!violations.isEmpty()) {
            throw new PipelineValidationException("Erro de validação no objeto: " + 
                violations.iterator().next().getMessage());
        }
    }

    private void validateSimple(br.com.orquestrator.orquestrator.domain.model.DataContract def, Object value) {
        if (def.formatRule() != null && value instanceof String str) {
            if (!Pattern.matches(def.formatRule(), str)) {
                throw new PipelineValidationException("Formato inválido para '" + def.contextKey() + "'");
            }
        }
    }
}
