package br.com.orquestrator.orquestrator.infra.logging;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;

/**
 * Plugin de Lookup para Log4j2 que extrai dados do ContextHolder (ScopedValues).
 * Permite usar ${ctx_val:correlation_id} ou ${ctx_val:node_id} no log4j2.xml.
 */
@Plugin(name = "ctx_val", category = StrLookup.CATEGORY)
public class ContextLookup extends AbstractLookup {

    @Override
    public String lookup(LogEvent event, String key) {
        if (key == null) {
            return null;
        }

        return switch (key) {
            case "correlation_id" -> ContextHolder.getCorrelationId().orElse("-");
            case "node_id" -> ContextHolder.getCurrentNode().orElse("-");
            default -> null;
        };
    }
}
