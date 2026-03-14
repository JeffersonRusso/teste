/*
package br.com.orquestrator.orquestrator.domain.model;

import br.com.orquestrator.orquestrator.core.ports.output.SemanticProvider;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import br.com.orquestrator.orquestrator.infra.semantic.ScriptedSemanticHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// CLASSE DESCONTINUADA: A lógica de registro semântico foi movida para um serviço no pacote 'core.engine.semantic'
// ou similar, para manter o domínio puro e evitar o uso de singletons estáticos.
@Slf4j
@Component
@RequiredArgsConstructor
public class SemanticRegistry {

    private final SemanticProvider semanticProvider;
    private final ExpressionEngine expressionEngine;
    private final Map<String, SemanticHandler> handlers = new ConcurrentHashMap<>();
    private static SemanticRegistry instance;

    @PostConstruct
    public void loadFromDatabase() {
        log.info("Carregando definições semânticas do banco...");
        
        semanticProvider.findAll().forEach(def -> {
            handlers.put(def.typeName().toUpperCase(), 
                new ScriptedSemanticHandler(
                    def.typeName(), 
                    def.formatScript(), 
                    def.validationScript(), 
                    expressionEngine
                )
            );
        });
        
        instance = this;
    }

    public static SemanticHandler getHandler(String typeName) {
        if (instance == null || typeName == null) return null;
        return instance.handlers.get(typeName.toUpperCase());
    }

    public void refresh() {
        handlers.clear();
        loadFromDatabase();
    }
}
*/
