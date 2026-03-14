package br.com.orquestrator.orquestrator.tasks.interceptor.impl.cache;

import br.com.orquestrator.orquestrator.api.task.TaskChain;
import br.com.orquestrator.orquestrator.api.task.TaskInterceptor;
import br.com.orquestrator.orquestrator.api.task.TaskResult;
import br.com.orquestrator.orquestrator.core.engine.binding.CompiledConfiguration;
import br.com.orquestrator.orquestrator.core.ports.output.DataFactory;
import br.com.orquestrator.orquestrator.domain.model.TaskExecutionContext;
import br.com.orquestrator.orquestrator.infra.cache.CacheEngine;
import br.com.orquestrator.orquestrator.infra.el.ExpressionEngine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public final class CacheInterceptor implements TaskInterceptor {

    private final ExpressionEngine expressionEngine;
    private final CacheEngine cacheEngine;
    private final CompiledConfiguration<CacheConfig> config;
    private final DataFactory dataFactory;
    private final ObjectMapper objectMapper;

    @Override
    public TaskResult intercept(TaskExecutionContext context, TaskChain chain) {
        String nodeId = context.getTaskName();
        var inputs = context.getInputs();
        
        try {
            CacheConfig resolvedConfig = config.resolve(inputs);
            
            // CORREÇÃO: Verifica se a chave de cache está configurada antes de compilar
            if (resolvedConfig.key() == null || resolvedConfig.key().isBlank()) {
                return chain.proceed(context);
            }

            String cacheKey = expressionEngine.compile(resolvedConfig.key()).evaluate(inputs, String.class);

            Optional<JsonNode> cached = cacheEngine.get(nodeId, cacheKey);
            if (cached.isPresent()) {
                log.debug("Cache HIT [{}] key [{}]", nodeId, cacheKey);
                return new TaskResult.Success(dataFactory.createValue(cached.get()), Map.of("cache_hit", true));
            }

            TaskResult result = chain.proceed(context);

            if (result instanceof TaskResult.Success s) {
                JsonNode bodyToCache = objectMapper.valueToTree(s.body().asNative());
                cacheEngine.put(nodeId, cacheKey, bodyToCache, resolvedConfig.ttlMs());
            }
            return result;

        } catch (Exception e) {
            log.warn("Falha ignorada na operação de cache para {}: {}", nodeId, e.getMessage());
            return chain.proceed(context);
        }
    }
}
