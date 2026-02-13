package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowConfigId implements Serializable {
    private String operationType;
    private Integer version;
}
