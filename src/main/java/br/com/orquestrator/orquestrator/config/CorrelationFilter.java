package br.com.orquestrator.orquestrator.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

/**
 * CorrelationFilter: Captura ou gera o ID de correlação e alimenta o MDC do Log4j2.
 */
@Component
public class CorrelationFilter implements Filter {
    
    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest httpRequest) {
            String correlationId = httpRequest.getHeader(CORRELATION_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }
            
            // Alimenta o MDC do Log4j2 para que apareça nos logs
            ThreadContext.put("correlationId", correlationId);
            try {
                chain.doFilter(request, response);
            } finally {
                ThreadContext.remove("correlationId");
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
