package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PipelineNodeDecoratorId implements Serializable {
    private UUID nodeId;
    private String templateId;
}
