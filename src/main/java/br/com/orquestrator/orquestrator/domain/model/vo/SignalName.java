package br.com.orquestrator.orquestrator.domain.model.vo;

import br.com.orquestrator.orquestrator.api.signal.Signal;

/**
 * SignalName: Identificador de um sinal de dados.
 */
public record SignalName(String name) implements Signal {
    public static SignalName of(String name) { return new SignalName(name); }
    @Override public String signalName() { return name; }
    @Override public String toString() { return name; }
}
