package br.com.orquestrator.orquestrator.adapter.persistence.repository.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import jakarta.persistence.Converter;

@Converter
public class JsonNodeConverter extends AbstractJsonConverter<JsonNode> {
    public JsonNodeConverter() {
        super(JsonNode.class);
    }

    @Override
    protected JsonNode defaultEmptyObject() {
        return JsonNodeFactory.instance.objectNode();
    }
}
