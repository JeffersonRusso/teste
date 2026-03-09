package br.com.orquestrator.orquestrator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PipelineNotFoundException extends RuntimeException {
    public PipelineNotFoundException(String operationType) {
        super("Nenhum pipeline ativo encontrado para a operação: " + operationType);
    }
}
