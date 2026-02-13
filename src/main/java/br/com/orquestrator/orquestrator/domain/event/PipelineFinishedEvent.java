package br.com.orquestrator.orquestrator.domain.event;

/**
 * Evento disparado quando um pipeline termina sua execução.
 */
public record PipelineFinishedEvent(PipelineExecutionSummary summary) {}
