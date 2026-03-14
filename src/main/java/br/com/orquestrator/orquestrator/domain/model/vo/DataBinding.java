package br.com.orquestrator.orquestrator.domain.model.vo;

/**
 * DataBinding: Representa a ligação entre um sinal e um ponteiro.
 */
public record DataBinding(SignalName signal, String pointer) {
    public DataBinding(String signalName, String dataPath) {
        this(SignalName.of(signalName), dataPath != null ? dataPath : "");
    }
    public String signalName() { return signal.name(); }
}
