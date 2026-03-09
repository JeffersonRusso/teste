//package br.com.orquestrator.orquestrator.core.context;
//
//import br.com.orquestrator.orquestrator.domain.ContextKey;
//import br.com.orquestrator.orquestrator.domain.vo .DataPath;
//
///**
// * ContextSchema: A autoridade sobre a estrutura do banco de contexto.
// * Agora retorna DataPath para garantir tipagem forte em todo o fluxo.
// */
//public final class ContextSchema {
//
//    private ContextSchema() {}
//
//    public static DataPath raw() { return DataPath.of(ContextKey.RAW); }
//    public static DataPath standard() { return DataPath.of(ContextKey.STANDARD); }
//    public static DataPath header() { return DataPath.of(ContextKey.HEADER); }
//    public static DataPath operationType() { return DataPath.of(ContextKey.OPERATION_TYPE); }
//    public static DataPath tags() { return DataPath.of("tags"); }
//
//    public static DataPath toStandardPath(String field) {
//        return DataPath.of(ContextKey.STANDARD + "." + field);
//    }
//
//    public static DataPath toNodeResultPath(String nodeId) {
//        return DataPath.of(nodeId);
//    }
//
//    public static DataPath toNodeStatusPath(String nodeId) {
//        return DataPath.of(nodeId + ".status");
//    }
//
//    public static DataPath toNodeErrorPath(String nodeId) {
//        return DataPath.of(nodeId + ".error");
//    }
//
//    public static String[] sovereignNamespaces() {
//        return new String[]{ContextKey.RAW, ContextKey.STANDARD, ContextKey.HEADER, "tags"};
//    }
//}
