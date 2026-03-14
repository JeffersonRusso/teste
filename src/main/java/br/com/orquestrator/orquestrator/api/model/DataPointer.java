//package br.com.orquestrator.orquestrator.api.model;
//
//import com.fasterxml.jackson.core.JsonPointer;
//import com.fasterxml.jackson.databind.JsonNode;
//
///**
// * DataPointer: Value Object que suporta tanto JSON Pointers (RFC 6901)
// * quanto nomes de campos planos.
// */
//public record DataPointer(String originalPath, JsonPointer pointer) {
//
//    public static final DataPointer ROOT = new DataPointer("", JsonPointer.empty());
//
//    public static DataPointer of(String path) {
//        if (path == null || path.isBlank()) {
//            return ROOT;
//        }
//
//        // Se começar com '/', é um JSON Pointer estrito
//        if (path.startsWith("/")) {
//            return new DataPointer(path, JsonPointer.compile(path));
//        }
//
//        // Caso contrário, é uma chave plana (convertemos para JSON Pointer)
//        return new DataPointer(path, JsonPointer.compile("/" + path));
//    }
//
//    public JsonNode navigate(JsonNode node) {
//        if (node == null) return com.fasterxml.jackson.databind.node.MissingNode.getInstance();
//        return node.at(pointer);
//    }
//
//    @Override
//    public String toString() {
//        return originalPath;
//    }
//}
