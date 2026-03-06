package br.com.orquestrator.orquestrator.domain.vo;

import java.util.Arrays;
import java.util.List;

public record DataPath(String value) {

    private static final String SEPARATOR = ".";
    private static final String SEPARATOR_REGEX = "\\.";

    public DataPath {
        if (value == null) value = "";
        value = value.trim();
    }

    public static DataPath of(String path) { return new DataPath(path); }

    public List<String> getParts() {
        if (value.isEmpty()) return List.of();
        return Arrays.asList(value.split(SEPARATOR_REGEX));
    }

    /** Retorna o caminho do pai. Ex: "a.b.c" -> "a.b" */
    public String getParentPath() {
        int index = value.lastIndexOf(SEPARATOR);
        return index == -1 ? "" : value.substring(0, index);
    }

    /** Retorna o nome da folha. Ex: "a.b.c" -> "c" */
    public String getLeafName() {
        int index = value.lastIndexOf(SEPARATOR);
        return index == -1 ? value : value.substring(index + 1);
    }

    public String getRoot() {
        int index = value.indexOf(SEPARATOR);
        return index == -1 ? value : value.substring(0, index);
    }

    public boolean provides(DataPath other) {
        if (this.equals(other)) return true;
        if (this.value.isEmpty()) return false;
        return other.value().startsWith(this.value + SEPARATOR);
    }

    public boolean isIdentity() { return value.isEmpty() || ".".equals(value); }

    @Override public String toString() { return value; }
}
