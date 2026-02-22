package br.com.orquestrator.orquestrator.core.pipeline;

import org.springframework.context.ApplicationEvent;

/**
 * Evento que sinaliza a necessidade de recarregar os metadados em memória.
 */
public class MetadataRefreshEvent extends ApplicationEvent {
    public MetadataRefreshEvent(Object source) {
        super(source);
    }
}
