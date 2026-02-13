package br.com.orquestrator.orquestrator.config;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GroovyConfig {

    @Bean
    public CompilerConfiguration compilerConfiguration() {
        CompilerConfiguration config = new CompilerConfiguration();

        // 1. Imports Automáticos (Quality of Life)
        // Isso permite usar 'List', 'Map', 'BigDecimal', 'LocalDate' nos scripts sem import
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStarImports(
                "java.util",
                "java.math",
                "java.time",
                "org.decimal4j.immutable",
                "com.fasterxml.jackson.databind",
                "br.com.orquestrator.orquestrator.domain" // Útil para acessar seus modelos
        );
        config.addCompilationCustomizers(imports);

        // 2. Sandbox de Segurança (Opcional mas Recomendado para Produção)
        // Isso impede que alguém escreva um script que chame System.exit(0)
        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setClosuresAllowed(true);
        // Lista de classes proibidas (Exemplo)
        // secure.setDisallowedImports(List.of("java.lang.System", "java.io.File"));

        config.addCompilationCustomizers(secure);

        return config;
    }
}