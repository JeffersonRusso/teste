package br.com.orquestrator.orquestrator.adapter.persistence.repository.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class ListStringConverter extends AbstractJsonConverter<List<String>> {
    public ListStringConverter() {
        super(new TypeReference<>() {
        });
    }

    @Override
    protected List<String> defaultEmptyObject() {
        return new ArrayList<>();
    }

    @Override
    protected String defaultEmptyJson() {
        return "[]";
    }
}
