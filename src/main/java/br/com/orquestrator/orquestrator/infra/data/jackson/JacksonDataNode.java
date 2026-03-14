package br.com.orquestrator.orquestrator.infra.data.jackson;

import br.com.orquestrator.orquestrator.domain.model.data.DataNode;
import br.com.orquestrator.orquestrator.domain.model.data.NavigationStrategy;
import br.com.orquestrator.orquestrator.domain.model.data.SmartNavigationStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * JacksonDataNode: Implementação de DataNode com Estratégia de Navegação Plugável.
 */
@RequiredArgsConstructor
public class JacksonDataNode implements DataNode {

    private final JsonNode delegate;
    private final ObjectMapper mapper;
    private final NavigationStrategy navigationStrategy = new SmartNavigationStrategy();

    @Override
    public DataNode find(String path) {
        return navigationStrategy.navigate(this, path);
    }

    @Override
    public DataNode at(String path) {
        if (path == null || path.isBlank()) return this;
        return new JacksonDataNode(delegate.at(path), mapper);
    }

    @Override
    public DataNode get(String field) {
        return new JacksonDataNode(delegate.path(field), mapper);
    }

    @Override
    public DataNode get(int index) {
        return new JacksonDataNode(delegate.path(index), mapper);
    }

    @Override
    public boolean isMissing() {
        return delegate == null || delegate.isMissingNode() || delegate.isNull();
    }

    @Override
    public boolean isObject() { return delegate != null && delegate.isObject(); }

    @Override
    public boolean isArray() { return delegate != null && delegate.isArray(); }

    @Override
    public boolean isValue() { return delegate != null && delegate.isValueNode(); }

    @Override
    public Optional<String> asText() {
        return isValue() ? Optional.of(delegate.asText()) : Optional.empty();
    }

    @Override
    public Optional<Integer> asInt() {
        return isValue() && delegate.isNumber() ? Optional.of(delegate.asInt()) : Optional.empty();
    }

    @Override
    public Optional<Double> asDouble() {
        return isValue() && delegate.isNumber() ? Optional.of(delegate.asDouble()) : Optional.empty();
    }

    @Override
    public Optional<Boolean> asBoolean() {
        return isValue() && delegate.isBoolean() ? Optional.of(delegate.asBoolean()) : Optional.empty();
    }

    @Override
    public Object asNative() {
        if (isMissing()) return null;
        return mapper.convertValue(delegate, Object.class);
    }

    @Override
    public String toString() {
        return delegate != null ? delegate.toString() : "null";
    }
}
