package br.com.orquestrator.orquestrator.tasks.interceptor.api;

/**
 * Contrato para provedores de interceptores.
 */
public interface InterceptorProvider<C> {
    String featureType();
    Class<C> configClass();
    
    /**
     * Cria o interceptor.
     * @param config Configuração tipada.
     * @param nodeId ID do nó que está sendo decorado.
     */
    TaskInterceptor create(C config, String nodeId);
}
