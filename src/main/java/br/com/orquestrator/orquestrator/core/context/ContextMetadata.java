package br.com.orquestrator.orquestrator.core.context;

import java.util.Set;

public interface ContextMetadata {
    String getCorrelationId();
    String getOperationType();
    Set<String> getTags();
}
