package br.com.orquestrator.orquestrator.infra.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "tb_operation_params")
public class OperationParamEntity {

    @Id
    @Column(name = "operation_type")
    private String operationType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "params_json")
    private Map<String, Object> params;
}