package br.com.orquestrator.orquestrator.config;

import br.com.orquestrator.orquestrator.domain.model.DataValue;
import br.com.orquestrator.orquestrator.domain.model.SemanticFormatter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SemanticConfig {

    private final SemanticFormatter formatter;

    @PostConstruct
    public void init() {
        // Conecta o mundo Spring com o mundo Records/ADT
        DataValue.FormatterHolder.setFormatter(formatter);
    }
}
