package br.com.orquestrator.orquestrator.api.signal;

/**
 * Signal: Contrato base para qualquer identificador de dado no fluxo.
 */
public interface Signal {
    /** Retorna o nome único do sinal. */
    String signalName();
}
