package br.com.orquestrator.orquestrator.config;

import br.com.orquestrator.orquestrator.infra.IdGenerator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlation_id";
    
    private final IdGenerator idGenerator;

    public MDCFilter(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest httpReq)) {
            chain.doFilter(request, response);
            return;
        }

        // Otimização: Evita try-finally se não for necessário (embora MDC exija limpeza)
        // O custo principal aqui é o MDC.put e o idGenerator.generateFastId()
        
        String correlationId = httpReq.getHeader(CORRELATION_ID_HEADER);
        boolean generated = false;
        
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = idGenerator.generateFastId();
            generated = true;
        }

        MDC.put(CORRELATION_ID_KEY, correlationId);
        
        try {
            if (generated && response instanceof HttpServletResponse httpRes) {
                httpRes.setHeader(CORRELATION_ID_HEADER, correlationId);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_KEY);
        }
    }
}
