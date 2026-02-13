package br.com.orquestrator.orquestrator.config;

import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do motor de regras DMN.
 */
@Configuration
public class DmnConfig {

    @Bean
    public DmnEngine dmnEngine() {
        // Utilizamos a configuração padrão do Camunda DMN Engine
        DefaultDmnEngineConfiguration configuration = (DefaultDmnEngineConfiguration) 
                DmnEngineConfiguration.createDefaultDmnEngineConfiguration();
        
        // Podemos customizar o motor aqui se necessário (ex: custom functions)
        return configuration.buildEngine();
    }
}
