package br.com.orquestrator.orquestrator.infra.semantic;

import br.com.orquestrator.orquestrator.domain.model.SemanticHandler;
import org.springframework.stereotype.Component;

@Component
public class CpfHandler implements SemanticHandler {
    @Override public String getTypeName() { return "CPF"; }

    @Override
    public String format(Object value) {
        if (value == null) return "";
        String s = value.toString();
        if (s.length() != 11) return s;
        return s.substring(0, 3) + ".***.***-" + s.substring(9);
    }
}
