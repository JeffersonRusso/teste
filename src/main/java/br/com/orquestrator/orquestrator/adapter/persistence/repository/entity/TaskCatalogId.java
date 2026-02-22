package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCatalogId implements Serializable {
    private String taskId;
    private Integer taskVersion; // Renomeado para evitar conflito com @Version do JPA
}
