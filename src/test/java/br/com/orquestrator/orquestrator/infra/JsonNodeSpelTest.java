package br.com.orquestrator.orquestrator.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.context.expression.MapAccessor;

import static org.assertj.core.api.Assertions.assertThat;

class JsonNodeSpelTest {

    private ExpressionParser parser;
    private ObjectMapper objectMapper;
    private StandardEvaluationContext context;

    @BeforeEach
    void setUp() {
        parser = new SpelExpressionParser();
        objectMapper = new ObjectMapper();
        context = new StandardEvaluationContext();
        JsonNodeAccessor accessor = new JsonNodeAccessor();
        context.addPropertyAccessor(new MapAccessor());
        context.addPropertyAccessor(accessor);
        context.addIndexAccessor(accessor);
    }

    @Test
    void shouldAccessJsonNodeProperties() throws Exception {
        String json = "{\"name\": \"John\", \"age\": 30, \"address\": {\"city\": \"New York\"}}";
        JsonNode node = objectMapper.readTree(json);
        context.setVariable("data", node);

        assertThat(parser.parseExpression("#data.name").getValue(context)).isEqualTo("John");
        assertThat(parser.parseExpression("#data.age").getValue(context)).isEqualTo(30);
        assertThat(parser.parseExpression("#data.address.city").getValue(context)).isEqualTo("New York");
    }

    @Test
    void shouldAccessArrayElements() throws Exception {
        String json = "{\"tags\": [\"java\", \"spring\", \"performance\"]}";
        JsonNode node = objectMapper.readTree(json);
        context.setVariable("data", node);

        assertThat(parser.parseExpression("#data.tags[0]").getValue(context)).isEqualTo("java");
        assertThat(parser.parseExpression("#data.tags[1]").getValue(context)).isEqualTo("spring");
        assertThat(parser.parseExpression("#data.tags.size").getValue(context)).isEqualTo(3);
    }

    @Test
    void shouldHandleMissingProperties() throws Exception {
        String json = "{\"name\": \"John\"}";
        JsonNode node = objectMapper.readTree(json);
        context.setVariable("data", node);

        assertThat(parser.parseExpression("#data.nonExistent").getValue(context)).isNull();
    }
}
