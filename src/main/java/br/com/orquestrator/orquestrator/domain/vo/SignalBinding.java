package br.com.orquestrator.orquestrator.domain.vo;

/**
 * SignalBinding: Representa a ligação entre um sinal de origem e um caminho de dados.
 * Substitui a concatenação de strings "/sinal/path".
 */
public record SignalBinding(
    String signalName,
    String dataPath
) {
    public SignalBinding {
        if (signalName == null || signalName.isBlank()) {
            throw new IllegalArgumentException("O nome do sinal é obrigatório.");
        }
        // Normaliza o path para sempre começar com / se existir
        if (dataPath != null && !dataPath.isBlank() && !dataPath.startsWith("/")) {
            dataPath = "/" + dataPath;
        }
    }

    public boolean hasPath() {
        return dataPath != null && !dataPath.isBlank();
    }
    
    @Override
    public String toString() {
        return hasPath() ? signalName + dataPath : signalName;
    }
}
