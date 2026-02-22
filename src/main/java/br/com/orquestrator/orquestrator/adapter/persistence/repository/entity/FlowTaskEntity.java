package br.com.orquestrator.orquestrator.adapter.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "tb_flow_tasks")
public class FlowTaskEntity {

    @EmbeddedId
    private FlowTaskId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("flowId")
    @JoinColumns({
        @JoinColumn(name = "operation_type", referencedColumnName = "operation_type"),
        @JoinColumn(name = "flow_version", referencedColumnName = "version")
    })
    private FlowConfigEntity flow;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(name = "task_id", referencedColumnName = "task_id", insertable = false, updatable = false),
        @JoinColumn(name = "task_version", referencedColumnName = "version", insertable = false, updatable = false)
    })
    private TaskCatalogEntity task;

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class FlowTaskId implements Serializable {
        @Column(name = "operation_type")
        private String operationType;
        @Column(name = "flow_version")
        private Integer flowVersion;
        @Column(name = "task_id")
        private String taskId;
        @Column(name = "task_version")
        private Integer taskVersion;
    }
}
