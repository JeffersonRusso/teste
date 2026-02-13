package br.com.orquestrator.orquestrator.adapter.web.controller;

import br.com.orquestrator.orquestrator.adapter.web.controller.dto.GraphResponse;
import br.com.orquestrator.orquestrator.core.pipeline.PipelineGraphService;
import br.com.orquestrator.orquestrator.tasks.registry.TaskRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PipelineGraphService graphService;
    private final TaskRegistry taskRegistry;

    @GetMapping("/pipelines/{operationType}/graph")
    public ResponseEntity<GraphResponse> getPipelineGraph(@PathVariable String operationType) {
        return ResponseEntity.ok(graphService.generateGraph(operationType));
    }
    
    @PostMapping("/tasks/refresh")
    public ResponseEntity<Void> refreshTasks() {
        taskRegistry.clearRegistry();
        return ResponseEntity.ok().build();
    }
}
