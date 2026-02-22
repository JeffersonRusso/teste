package br.com.orquestrator.orquestrator.core.context.init;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
@RequiredArgsConstructor
public class TimeoutContextInitializer implements ContextTaskInitializer {

    @Override
    public void initialize(ExecutionContext context) {
        // Deadline removido do contexto minimalista. 
    }
}
