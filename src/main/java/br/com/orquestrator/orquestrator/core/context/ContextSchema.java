package br.com.orquestrator.orquestrator.core.context;

import br.com.orquestrator.orquestrator.domain.ContextKey;

/**
 * ContextSchema: A única autoridade sobre a estrutura e endereçamento do banco de contexto.
 */
public final class ContextSchema {

    private ContextSchema() {}

    /** Namespaces principais */
    public static String raw() { return ContextKey.RAW; }
    public static String standard() { return ContextKey.STANDARD; }
    public static String header() { return ContextKey.HEADER; }
    public static String operationType() { return ContextKey.OPERATION_TYPE; }

    /** Constrói caminhos de dados */
    public static String toStandardPath(String field) {
        return standard() + "." + field;
    }

    /** Constrói caminhos de execução de nós */
    public static String toNodeResultPath(String nodeId) {
        return nodeId; // O resultado bruto do nó fica na raiz com o nome do nó
    }

    public static String toNodeStatusPath(String nodeId) {
        return nodeId + ".status";
    }

    public static String toNodeErrorPath(String nodeId) {
        return nodeId + ".error";
    }

    /** Retorna as chaves que devem ser expostas como variáveis no SpEL */
    public static String[] sovereignNamespaces() {
        return new String[]{raw(), standard(), header()};
    }
}
