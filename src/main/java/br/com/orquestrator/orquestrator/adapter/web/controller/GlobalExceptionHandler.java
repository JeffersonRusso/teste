package br.com.orquestrator.orquestrator.adapter.web.controller;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.exception.PipelineException;
import br.com.orquestrator.orquestrator.exception.PipelineValidationException;
import br.com.orquestrator.orquestrator.exception.TaskConfigurationException;
import br.com.orquestrator.orquestrator.exception.TaskExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Optional;

/**
 * GlobalExceptionHandler: Centraliza o tratamento de erros da API.
 * Segue o padrão RFC 7807 (Problem Details for HTTP APIs).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TYPE_BASE = "https://api.orquestrator.com.br/errors/";

    @ExceptionHandler(PipelineValidationException.class)
    public ResponseEntity<ProblemDetail> handle(PipelineValidationException e) {
        return createResponse(HttpStatus.UNPROCESSABLE_ENTITY, "validation-error", "Erro de Validação", e);
    }

    @ExceptionHandler(TaskExecutionException.class)
    public ResponseEntity<ProblemDetail> handle(TaskExecutionException e) {
        log.error("Falha na execução da task [{}]: {}", e.getNodeId(), e.getMessage());
        return createResponse(HttpStatus.BAD_GATEWAY, "execution-error", "Erro de Execução", e);
    }

    @ExceptionHandler(TaskConfigurationException.class)
    public ResponseEntity<ProblemDetail> handle(TaskConfigurationException e) {
        log.error("Falha de configuração: {}", e.getMessage());
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "config-error", "Erro de Configuração", e);
    }

    @ExceptionHandler(PipelineException.class)
    public ResponseEntity<ProblemDetail> handle(PipelineException e) {
        log.error("Erro no pipeline: {}", e.getMessage());
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "pipeline-error", "Erro de Fluxo", e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception e) {
        log.error("Erro não mapeado: {}", e.getMessage(), e);
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "Erro Interno do Servidor", e);
    }

    private ResponseEntity<ProblemDetail> createResponse(HttpStatus status, String code, String title, Exception e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problem.setType(URI.create(TYPE_BASE + code));
        problem.setTitle(title);
        
        // Injeta metadados de rastreabilidade se o contexto estiver disponível
        ContextHolder.getContext().ifPresent(ctx -> {
            problem.setProperty("correlationId", ctx.metadata().getCorrelationId());
            problem.setProperty("operation", ctx.metadata().getOperationType());
        });

        // Injeta metadados da exceção
        if (e instanceof PipelineException pe) {
            Optional.ofNullable(pe.getNodeId()).ifPresent(id -> problem.setProperty("nodeId", id));
        }

        return ResponseEntity.status(status).body(problem);
    }
}
