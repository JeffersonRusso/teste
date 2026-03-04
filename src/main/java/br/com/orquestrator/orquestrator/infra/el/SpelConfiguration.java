package br.com.orquestrator.orquestrator.infra.el;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Configuration
public class SpelConfiguration {

    @Bean
    public SpelExpressionParser spelExpressionParser() {
        // CONFIGURAÇÃO MÁGICA: autoGrowNestedPaths = true
        // Isso faz o SpEL criar os Maps intermediários automaticamente no put!
        SpelParserConfiguration config = new SpelParserConfiguration(true, true);
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
