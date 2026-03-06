package br.com.orquestrator.orquestrator.infra.format;

import br.com.orquestrator.orquestrator.domain.model.SemanticFormatter;
import org.springframework.stereotype.Component;

@Component
public class DefaultSemanticFormatter implements SemanticFormatter {

    @Override
    public String format(String typeName, Object value) {
        if (value == null) return "";
        String raw = value.toString();

        return switch (typeName.toUpperCase()) {
            case "CPF" -> maskCpf(raw);
            case "EMAIL" -> maskEmail(raw);
            default -> raw;
        };
    }

    private String maskCpf(String cpf) {
        if (cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return email;
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }
}
