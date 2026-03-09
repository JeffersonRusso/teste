package br.com.orquestrator.orquestrator.core.engine.runtime;

import java.util.HashSet;
import java.util.Set;

/**
 * SignalSchema: Representação estática do SignalRegistry para validação de contratos.
 * Agora usa Strings puras para validação de provisão.
 */
public class SignalSchema {
    private final Set<String> available = new HashSet<>();

    public void register(String path) {
        if (path != null && !path.isBlank()) {
            available.add(path);
        }
    }

    /**
     * Verifica se um caminho solicitado pode ser satisfeito pelos sinais registrados.
     */
    public boolean canProvide(String requiredPath) {
        if (requiredPath == null || requiredPath.isBlank()) return false;
        
        // Normaliza para garantir comparação correta (assume padrão JSON Pointer)
        String req = requiredPath.startsWith("/") ? requiredPath : "/" + requiredPath;
        
        return available.stream().anyMatch(prov -> {
            String p = prov.startsWith("/") ? prov : "/" + prov;
            // Um caminho é satisfeito se ele mesmo existe, ou se um pai/filho existe.
            // Ex: /cliente é satisfeito por /cliente/id
            // Ex: /cliente/id é satisfeito por /cliente
            return p.equals(req) || req.startsWith(p + "/") || p.startsWith(req + "/");
        });
    }
}
