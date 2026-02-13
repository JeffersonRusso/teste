package br.com.orquestrator.orquestrator.infra.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.IndexAccessor;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

/**
 * Permite que o SpEL acesse propriedades de um JsonNode do Jackson
 * como se fossem propriedades de um Bean ou chaves de um Map.
 * 
 * Também suporta acesso por índice em Arrays.
 * 
 * Ex: #jsonNode.nome ou #jsonNode.endereco.rua ou #jsonNode.tags[0]
 */
public class JsonNodeAccessor implements PropertyAccessor, IndexAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[] { JsonNode.class, ObjectNode.class, ArrayNode.class };
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
        return target instanceof JsonNode;
    }

    // --- Métodos de IndexAccessor (para suporte a [0]) ---

    @Override
    public boolean canRead(EvaluationContext context, Object target, Object index) throws AccessException {
        return target instanceof JsonNode && (index instanceof Integer || index instanceof String);
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, Object index) throws AccessException {
        JsonNode node = (JsonNode) target;
        if (index instanceof Integer i) {
            return new TypedValue(unwrap(node.get(i)));
        }
        return new TypedValue(unwrap(node.get(index.toString())));
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, Object index) throws AccessException {
        return false;
    }

    @Override
    public void write(EvaluationContext context, Object target, Object index, Object newValue) throws AccessException {
        throw new UnsupportedOperationException("Escrita por índice em JsonNode não suportada.");
    }

    // --- Métodos de PropertyAccessor (para suporte a .propriedade) ---

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        JsonNode node = (JsonNode) target;
        
        // Se for um array e o "name" for um número, tenta acessar o índice
        if (node.isArray()) {
            try {
                int index = Integer.parseInt(name);
                return new TypedValue(unwrap(node.get(index)));
            } catch (NumberFormatException e) {
                // Se não for número, talvez queira uma propriedade especial como 'size'
                if ("size".equals(name) || "length".equals(name)) {
                    return new TypedValue(node.size());
                }
            }
        }

        JsonNode value = node.get(name);
        return new TypedValue(unwrap(value));
    }

    /**
     * Converte tipos primitivos do Jackson para tipos nativos do Java
     * para facilitar a vida do SpEL (comparações, etc).
     */
    private Object unwrap(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isNumber()) {
            return node.numberValue();
        }
        // Se for objeto ou array, retorna o próprio node para permitir navegação contínua
        return node;
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
        return false;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
        throw new UnsupportedOperationException("Escrita em JsonNode via SpEL não suportada.");
    }
}
