package br.com.orquestrator.orquestrator.tasks.interceptor.api;

/**
 * DecoratorFactory: Responsável por criar instâncias de interceptores baseadas em configuração.
 */
public interface DecoratorFactory<C> {
    
    String getType();

    Class<C> getConfigClass();

    /**
     * Cria o interceptor configurado (Padrão Linear).
     */
    TaskInterceptor create(C config, String nodeId);
}
