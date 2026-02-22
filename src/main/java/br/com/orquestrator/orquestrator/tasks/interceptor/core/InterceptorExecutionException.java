package br.com.orquestrator.orquestrator.tasks.interceptor.core;

public class InterceptorExecutionException extends RuntimeException {
    public InterceptorExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
