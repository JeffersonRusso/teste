package br.com.orquestrator.orquestrator.tasks.interceptor.api;

/**
 * DecoratorFactory: Responsável por criar instâncias de decoradores baseadas em configuração.
 */
public interface DecoratorFactory<C> {
    
    /**
     * O identificador único deste decorador (ex: 'RETRY', 'CACHE').
     */
    String getType();

    /**
     * A classe de configuração que este decorador espera.
     */
    Class<C> getConfigClass();

    /**
     * Cria o decorador configurado.
     */
    TaskDecorator create(C config, String nodeId);
}
