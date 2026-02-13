package br.com.orquestrator.orquestrator.domain.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Representa um caminho de dados (ex: standard.customer.id).
 * Centraliza toda a lógica de manipulação de strings com pontos.
 */
public record Path(String value) {

    public Path {
        Objects.requireNonNull(value, "O caminho não pode ser nulo");
    }

    public static Path of(String value) {
        return new Path(value);
    }

    public boolean isNested() {
        return value.contains(".");
    }

    public String root() {
        if (!isNested()) return value;
        return value.substring(0, value.indexOf('.'));
    }

    public String lastSegment() {
        if (!isNested()) return value;
        return value.substring(value.lastIndexOf('.') + 1);
    }

    /**
     * Retorna a hierarquia de caminhos.
     * "a.b.c" -> ["a", "a.b", "a.b.c"]
     */
    public List<Path> hierarchy() {
        List<Path> paths = new ArrayList<>();
        String current = value;
        paths.add(this);
        while (current.contains(".")) {
            current = current.substring(0, current.lastIndexOf('.'));
            paths.add(new Path(current));
        }
        return paths;
    }

    public String[] segments() {
        return value.split("\\.");
    }

    @Override
    public String toString() {
        return value;
    }
}
