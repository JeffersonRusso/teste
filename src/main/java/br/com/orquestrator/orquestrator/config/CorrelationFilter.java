package br.com.orquestrator.orquestrator.config;

import br.com.orquestrator.orquestrator.core.context.ContextHolder;
import br.com.orquestrator.orquestrator.infra.IdGenerator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro de entrada ultra-performático para Correlation ID.
 * Evita o overhead de ScopedValue.run() na thread do Servlet, 
 * delegando a propagação para o ponto de entrada do pipeline.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class CorrelationFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private final IdGenerator idGenerator;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest httpReq)) {
            chain.doFilter(request, response);
            return;
        }

        // 1. Resolve o ID de forma rápida
        String correlationId = httpReq.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = idGenerator.generateFastId();
        }

        // 2. Adiciona ao header de resposta
        if (response instanceof HttpServletResponse httpRes) {
            httpRes.setHeader(CORRELATION_ID_HEADER, correlationId);
        }

        // 3. Armazena no ContextHolder (que agora usará ThreadLocal para a thread do Servlet)
        ContextHolder.setTempCorrelationId(correlationId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            ContextHolder.clearTempCorrelationId();
        }
    }
}
