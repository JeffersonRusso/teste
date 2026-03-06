package br.com.orquestrator.orquestrator.tasks.base;

/**
 * Configurable: Interface de capacidade que define que uma Task exige configuração tipada.
 * SOLID: Interface Segregation Principle.
 */
public interface Configurable<C> {
    /**
     * Retorna a classe de configuração esperada pela task.
     */
    Class<C> getConfigClass();
}
