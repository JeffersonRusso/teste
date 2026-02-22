package br.com.orquestrator.orquestrator.domain.model;

public record DataSpec(
    String name,
    DataType type,
    boolean optional,
    String path // Expressão para extração (ex: "numeros[0].celular")
) {
    public static DataSpec of(String name) {
        return new DataSpec(name, DataType.ANY, false, null);
    }

    public static DataSpec fromMapping(DataMapping mapping) {
        if (mapping == null) return null;
        return new DataSpec(
            mapping.name(),
            DataType.from(mapping.type()),
            false,
            mapping.path()
        );
    }
}
