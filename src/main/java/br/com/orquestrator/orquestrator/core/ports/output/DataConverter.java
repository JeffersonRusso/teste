package br.com.orquestrator.orquestrator.core.ports.output;

/**
 * DataConverter: Porta de saída para conversão de tipos entre mapas e objetos ricos.
 * Isola o Core de bibliotecas como Jackson ou Gson.
 */
public interface DataConverter {
    
    /**
     * Converte um objeto (geralmente Map ou String) para o tipo destino solicitado.
     */
    <T> T convert(Object source, Class<T> targetType);
}
