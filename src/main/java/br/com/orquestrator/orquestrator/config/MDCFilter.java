package br.com.orquestrator.orquestrator.config;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * MDCFilter: Desativado temporariamente para investigar pinning de Virtual Threads.
 */
@Component
public class MDCFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}
