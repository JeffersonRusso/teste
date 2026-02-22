package br.com.orquestrator.orquestrator.domain.model;

public enum DataType {
    STRING,
    INTEGER,
    DECIMAL,
    BOOLEAN,
    JSON,
    ANY;

    public static DataType from(String value) {
        if (value == null) return ANY;
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ANY;
        }
    }
}
