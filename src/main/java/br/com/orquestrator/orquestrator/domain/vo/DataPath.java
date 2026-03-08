package br.com.orquestrator.orquestrator.domain.vo;

/**
 * DataPath: Value Object de ultra-performance.
 * Pré-calcula tudo no startup para garantir alocação ZERO no caminho quente.
 */
public final class DataPath {

    private final String value;
    private final String root;
    private final DataPath subPathObject; // O segredo: sub-path já é um objeto
    private final String leafName;
    private final String[] parts;
    private final boolean signalOnly;

    private static final String SEPARATOR = ".";
    private static final String SEPARATOR_REGEX = "\\.";

    private DataPath(String value, boolean isInternal) {
        this.value = value != null ? value.trim() : "";
        this.parts = this.value.isEmpty() ? new String[0] : this.value.split(SEPARATOR_REGEX);
        
        int firstDot = this.value.indexOf(SEPARATOR);
        String subPathStr;
        if (firstDot == -1) {
            this.root = this.value;
            subPathStr = ".";
            this.subPathObject = null;
            this.signalOnly = true;
        } else {
            this.root = this.value.substring(0, firstDot);
            subPathStr = this.value.substring(firstDot + 1);
            // Recursão controlada: cria o objeto do sub-path apenas uma vez
            this.subPathObject = isInternal ? null : new DataPath(subPathStr, true);
            this.signalOnly = false;
        }

        int lastDot = this.value.lastIndexOf(SEPARATOR);
        this.leafName = lastDot == -1 ? this.value : this.value.substring(lastDot + 1);
    }

    public static DataPath of(String path) {
        return new DataPath(path, false);
    }

    public String getRoot() { return root; }
    public DataPath getSubPathObject() { return subPathObject; }
    public String getLeafName() { return leafName; }
    public String[] getParts() { return parts; }
    public boolean isSignalOnly() { return signalOnly; }
    public String value() { return value; }

    public boolean provides(DataPath other) {
        if (this.value.equals(other.value)) return true;
        if (this.value.isEmpty()) return false;
        return other.value.startsWith(this.value + SEPARATOR);
    }

    public boolean isEmpty() { return value.isEmpty() || ".".equals(value); }

    @Override public String toString() { return value; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPath dataPath)) return false;
        return value.equals(dataPath.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
}
