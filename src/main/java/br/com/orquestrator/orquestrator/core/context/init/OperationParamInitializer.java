package br.com.orquestrator.orquestrator.core.context.init;

import br.com.orquestrator.orquestrator.domain.vo.ExecutionContext;
import br.com.orquestrator.orquestrator.infra.repository.OperationParamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Inicializador que carrega parâmetros fixos da operação para o contexto.
 * Agora com cache para evitar idas ao banco em cada requisição.
 */
@Slf4j
@Component("operationParamInitializer")
@RequiredArgsConstructor
public class OperationParamInitializer implements ContextTaskInitializer {

    private final OperationParamRepository repository;

    @Override
    public void initialize(ExecutionContext context) {
        fetchParams(context.getOperationType())
                .ifPresent(params -> {
                    log.debug("Carregando parâmetros operacionais para: {}", context.getOperationType());
                    context.put("operation_params", params);
                });
    }

    @Cacheable(value = "operation_params", key = "#operationType")
    public java.util.Optional<java.util.Map<String, Object>> fetchParams(String operationType) {
        return repository.findById(operationType)
                .map(entity -> entity.getParams());
    }
}
