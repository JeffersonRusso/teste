package br.com.orquestrator.orquestrator.domain.vo;

import java.util.Set;
import java.util.TreeSet;

/**
 * PipelineIdentity: Identidade única de um cenário de execução.
 * Usado como chave de cache para pipelines compilados.
 */
public record PipelineIdentity(
    String operationType,
    int version,
    Set<String> tags
) {
    public PipelineIdentity {
        // Garante que as tags estejam sempre ordenadas para que o hashCode seja consistente
        // independente da ordem em que as tags foram adicionadas.
        tags = new TreeSet<>(tags);
    }
}
