package br.com.orquestrator.orquestrator.infra.el;

import br.com.orquestrator.orquestrator.infra.json.MapToJsonStringConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Centraliza a infraestrutura do SpEL para trabalhar com Maps.
 */
@Configuration
public class SpelConfiguration {

    @Bean
    @Primary
    public ExpressionParser orchestratorExpressionParser() {
        var config = new SpelParserConfiguration(SpelCompilerMode.MIXED, this.getClass().getClassLoader());
        return new SpelExpressionParser(config);
    }

    @Bean
    public TemplateParserContext templateParserContext() {
        return new TemplateParserContext();
    }

    @Bean
    @Primary
    public DefaultConversionService spelConversionService(MapToJsonStringConverter mapConverter) {
        var service = new DefaultConversionService();
        service.addConverter(mapConverter);
        return service;
    }

    @Bean
    public MapAccessor mapAccessor() {
        return new MapAccessor();
    }
}
