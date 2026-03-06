package br.com.orquestrator.orquestrator.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * CorrelationFilter: Otimizado para 100k TPS.
 * Evita o uso de UUID.randomUUID() que é bloqueante.
 */
@Component
public class CorrelationFilter implements Filter {
    
    private static final String CORRELATION_HEADER = "X-Correlation-ID";
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest httpRequest) {
            String correlationId = httpRequest.getHeader(CORRELATION_HEADER);
            
            // Se não vier no header, gera um ID rápido (não-bloqueante)
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = generateFastId();
            }
            
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

    /**
     * Gera um ID alfanumérico rápido usando ThreadLocalRandom.
     * Muito mais performático que UUID.randomUUID() em alta concorrência.
     */
    private String generateFastId() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        char[] id = new char[16];
        for (int i = 0; i < 16; i++) {
            id[i] = HEX_CHARS[random.nextInt(16)];
        }
        return new String(id);
    }
}
