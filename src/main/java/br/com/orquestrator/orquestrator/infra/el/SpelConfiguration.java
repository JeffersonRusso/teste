package br.com.orquestrator.orquestrator.infra.el;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * SpelConfiguration: Registra os componentes base para o motor de expressões.
 */
@Configuration
public class SpelConfiguration {

    @Bean
    public MapAccessor mapAccessor() {
        return new MapAccessor();
    }

    @Bean
    public DefaultConversionService spelConversionService() {
        return new DefaultConversionService();
    }
}
