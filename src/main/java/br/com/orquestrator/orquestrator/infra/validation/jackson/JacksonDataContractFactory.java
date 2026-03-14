package br.com.orquestrator.orquestrator.infra.validation.jackson;

import br.com.orquestrator.orquestrator.core.engine.validation.DataContract;
import br.com.orquestrator.orquestrator.core.ports.output.DataContractFactory;
import org.springframework.stereotype.Component;

/**
 * JacksonDataContractFactory: Implementação que fornece contratos baseados em Jackson.
 */
@Component
public class JacksonDataContractFactory implements DataContractFactory {

    @Override
    public DataContract create(String key, String schemaDefinition) {
        return new JacksonDataContract(key, schemaDefinition);
    }
}
