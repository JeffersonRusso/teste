package br.com.orquestrator.orquestrator.tasks.http;

import br.com.orquestrator.orquestrator.exception.TaskExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class HttpErrorHandler {

    public void handle(Exception e, String nodeId, String url, String method) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        
        String friendlyMessage = getFriendlyMessage(e);
        String technicalDetails = extractDetailedError(e);
        
        throw new TaskExecutionException(String.format("%s (%s)", friendlyMessage, technicalDetails), e)
                .withNodeId(nodeId)
                .addMetadata("url", url)
                .addMetadata("method", method)
                .addMetadata("errorType", e.getClass().getSimpleName());
    }

    private String getFriendlyMessage(Throwable e) {
        if (e instanceof HttpTimeoutException || e instanceof TimeoutException  ) {
            return "Timeout na comunicação";
        }
        if (e instanceof ConnectException || e instanceof ClosedChannelException) {
            return "Falha de conexão (Serviço indisponível ou recusou conexão)";
        }
        return "Erro na requisição HTTP";
    }

    private String extractDetailedError(Throwable e) {
        if (e == null) return "Unknown";
        
        String msg = e.getMessage();
        if (msg == null || msg.isBlank() || "null".equalsIgnoreCase(msg)) {
            if (e.getCause() != null) {
                return String.format("%s -> %s", e.getClass().getSimpleName(), extractDetailedError(e.getCause()));
            }
            return e.getClass().getSimpleName();
        }
        return msg;
    }
}
