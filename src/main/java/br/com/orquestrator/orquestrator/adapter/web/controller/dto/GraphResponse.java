package br.com.orquestrator.orquestrator.adapter.web.controller.dto;

import java.util.List;

public record GraphResponse(List<Node> nodes, List<Edge> edges) {
    public record Node(String id, String label, String type) {}
    public record Edge(String source, String target, String label) {}
}
