package br.com.orquestrator.orquestrator.infra.el;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Configuration
public class SpelConfiguration {

    @Bean
    public SpelExpressionParser spelExpressionParser() {
        // CONFIGURAÇÃO DE ELITE: Compilação imediata para Bytecode
        // Isso transforma a expressão SpEL em código Java nativo após algumas execuções.
        SpelParserConfiguration config = new SpelParserConfiguration(
            SpelCompilerMode.IMMEDIATE, 
            this.getClass().getClassLoader()
        );
        return new SpelExpressionParser(config);
    }

    @Bean
    public MapAccessor mapAccessor() {
        return new MapAccessor();
    }

    @Bean
    public DefaultConversionService spelConversionService() {
        return new DefaultConversionService();
    }
}
