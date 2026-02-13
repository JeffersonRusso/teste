package br.com.orquestrator.orquestrator.infra.logging;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.springframework.stereotype.Component;

/**
 * Garante que o Log4j2 encontre nossos plugins customizados.
 */
@Component
public class LoggingInitializer {

    @PostConstruct
    public void init() {
        // Força o Log4j2 a escanear o pacote de logging
        PluginManager.addPackage("br.com.orquestrator.orquestrator.infra.logging");
    }
}
