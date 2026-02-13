package br.com.orquestrator.orquestrator.adapter.persistence.repository;

import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.FeatureTemplateEntity;
import br.com.orquestrator.orquestrator.adapter.persistence.repository.entity.InfraProfileEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provedor de Catálogo de Features e Perfis de Infraestrutura.
 * Implementa a estratégia de carregamento em duas etapas (Warmup em memória).
 * Java 21: Utiliza SequencedCollections e imutabilidade para alta performance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureCatalogProvider {

    private final FeatureTemplateRepository templateRepo;
    private final InfraProfileRepository profileRepo;

    // Caches locais imutáveis para acesso ultra-rápido durante a montagem do pipeline
    private volatile Map<String, FeatureTemplateEntity> templateCache = Collections.emptyMap();
    private volatile Map<String, InfraProfileEntity> profileCache = Collections.emptyMap();

    /**
     * Carga inicial de dados. Chamada pelo OrchestratorBootstrapper.
     */
    public void loadInitialData() {
        log.info("Carregando catálogo de suporte (Templates e Perfis)...");
        refresh();
    }

    /**
     * Atualiza os caches em memória a partir do banco de dados.
     * Executado periodicamente via agendamento.
     */
    @Scheduled(fixedDelayString = "${app.catalog.refresh-delay:60000}")
    public void refresh() {
        log.debug("Sincronizando catálogo de suporte com o banco de dados...");
        
        try {
            // Java 21: toUnmodifiableMap garante que o cache seja imutável e thread-safe para leitura
            this.templateCache = templateRepo.findAll().stream()
                .collect(Collectors.toUnmodifiableMap(FeatureTemplateEntity::getTemplateId, t -> t));
                
            this.profileCache = profileRepo.findAll().stream()
                .collect(Collectors.toUnmodifiableMap(InfraProfileEntity::getProfileId, p -> p));
            
            log.info(STR."Catálogo sincronizado: \{templateCache.size()} templates, \{profileCache.size()} perfis.");
        } catch (Exception e) {
            log.warn("Aviso: Não foi possível sincronizar o catálogo (Banco pode estar inicializando).");
        }
    }

    public Optional<FeatureTemplateEntity> getTemplate(String id) {
        return Optional.ofNullable(templateCache.get(id));
    }

    public Optional<InfraProfileEntity> getProfile(String id) {
        return Optional.ofNullable(profileCache.get(id));
    }
}
