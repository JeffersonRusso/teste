package br.com.orquestrator.orquestrator.core.engine.runtime;

import br.com.orquestrator.orquestrator.domain.vo.DataPath;
import java.util.HashSet;
import java.util.Set;

/**
 * SignalSchema: Representação estática do SignalRegistry para validação de contratos.
 * Centraliza a inteligência de "quem provê o quê" no grafo de sinais.
 */
public class SignalSchema {
    private final Set<DataPath> available = new HashSet<>();

    public void register(String path) {
        if (path != null && !path.isBlank()) {
            available.add(DataPath.of(path));
        }
    }

    /**
     * Verifica se um caminho solicitado pode ser satisfeito pelos sinais registrados.
     */
    public boolean canProvide(String requiredPath) {
        DataPath req = DataPath.of(requiredPath);
        // Um caminho é satisfeito se ele mesmo existe, se um pai existe (container)
        // ou se ele é um prefixo de algo que existe (provisão parcial).
        return available.stream().anyMatch(prov -> prov.provides(req) || req.provides(prov));
    }
}
