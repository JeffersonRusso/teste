//package br.com.orquestrator.orquestrator.domain.model;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.node.MissingNode;
//
///**
// * DataPointer: Utilitário de Domínio para navegação em estruturas de dados (JSON).
// */
//public record DataPointer(String path) {
//
//    public static DataPointer of(String path) {
//        return new DataPointer(path);
//    }
//
//    public JsonNode navigate(JsonNode root) {
//        if (root == null) return MissingNode.getInstance();
//        if (path == null || path.isBlank()) return root;
//
//        // Implementação simplificada de navegação (JSON Pointer ou chave direta)
//        if (path.startsWith("/")) {
//            return root.at(path);
//        }
//        return root.path(path);
//    }
//
//    public boolean isRoot() {
//        return path == null || path.isBlank();
//    }
//}
