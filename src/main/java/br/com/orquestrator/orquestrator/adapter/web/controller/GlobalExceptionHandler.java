package br.com.orquestrator.orquestrator.adapter.web.controller;

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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String URN_PREFIX = "urn:problem:";

    @ExceptionHandler(PipelineValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidation(PipelineValidationException e) {
        log.warn("Erro de validação de pipeline: {}", e.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, e, "pipeline-validation", "Pipeline Validation Error");
    }

    @ExceptionHandler(TaskExecutionException.class)
    public ResponseEntity<ProblemDetail> handleExecution(TaskExecutionException e) {
        log.error("Erro de execução de task: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.BAD_GATEWAY, e, "task-execution", "Task Execution Error");
    }

    @ExceptionHandler(TaskConfigurationException.class)
    public ResponseEntity<ProblemDetail> handleConfiguration(TaskConfigurationException e) {
        log.error("Erro de configuração de task: {}", e.getMessage(), e);
        // Expondo mensagem para facilitar debug de configuração
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e, "task-configuration", "Configuration Error");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception e) {
        log.error("Erro inesperado: {}", e.getMessage(), e);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado.");
        problem.setType(URI.create(URN_PREFIX + "internal-error"));
        problem.setTitle("Internal Server Error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ResponseEntity<ProblemDetail> buildResponse(HttpStatus status, Exception e, String typeSuffix, String title) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problem.setType(URI.create(URN_PREFIX + typeSuffix));
        problem.setTitle(title);

        if (e instanceof PipelineException pe && pe.getNodeId() != null) {
            problem.setProperty("nodeId", pe.getNodeId());
        }

        return ResponseEntity.status(status).body(problem);
    }
}
