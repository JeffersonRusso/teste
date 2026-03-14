package br.com.orquestrator.orquestrator.core.ports.output;

import br.com.orquestrator.orquestrator.domain.rules.DataContract;

import java.util.Optional;

/**
 * DataContractProvider: Porta de saída para prover contratos de dados.
 * O Domínio solicita os contratos por meio dessa interface.
 */
public interface DataContractProvider {
    
    /**
     * Busca um contrato de dados pela sua chave única.
     * Pode vir do Banco de Dados, Arquivo, Cache, etc.
     */
    Optional<DataContract> findByKey(String key);
}
