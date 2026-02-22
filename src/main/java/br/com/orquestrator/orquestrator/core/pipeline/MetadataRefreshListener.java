package br.com.orquestrator.orquestrator.core.pipeline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataRefreshListener {

    private final MetadataLoader loader;

    @EventListener(MetadataRefreshEvent.class)
    public void onRefresh() {
        log.info("Estímulo de atualização recebido. Recarregando metadados...");
        loader.reloadAll();
    }
}
