package br.com.orquestrator.orquestrator.domain.vo;

import br.com.orquestrator.orquestrator.infra.cache.CacheFactory;
import com.github.benmanes.caffeine.cache.Cache;

/**
 * DataPath: Value Object de ultra-performance.
 * Usa Caffeine para interning seguro e monitorado.
 */
public final class DataPath {

    private static final Cache<String, DataPath> CACHE = CacheFactory.createHotCache(1024);

    private final String value;
    private final String root;
    private final DataPath subPathObject;
    private final String leafName;
    private final String[] parts;
    private final boolean signalOnly;

    private static final String SEPARATOR = ".";
    private static final String SEPARATOR_REGEX = "\\.";

    private DataPath(String value, boolean isInternal) {
        this.value = value != null ? value.trim() : "";
        this.parts = this.value.isEmpty() ? new String[0] : this.value.split(SEPARATOR_REGEX);

        int firstDot = this.value.indexOf(SEPARATOR);

        if (firstDot == -1) {
            this.root = this.value;
            this.subPathObject = null;
            this.signalOnly = true;
        } else {
            this.root = this.value.substring(0, firstDot);
            String subPathStr = this.value.substring(firstDot + 1);
            // Recursão controlada: usa o cache para o sub-path também!
            this.subPathObject = isInternal ? null : DataPath.of(subPathStr);
            this.signalOnly = false;
        }

        int lastDot = this.value.lastIndexOf(SEPARATOR);
        this.leafName = lastDot == -1 ? this.value : this.value.substring(lastDot + 1);
    }

    public static DataPath of(String path) {
        String key = (path == null) ? "" : path;
        return CACHE.get(key, k -> new DataPath(k, false));
    }

    public String getRoot() { return root; }
    public DataPath getSubPathObject() { return subPathObject; }
    public String getLeafName() { return leafName; }
    public String[] getParts() { return parts; }
    public boolean isSignalOnly() { return signalOnly; }
    public String value() { return value; }

    public boolean provides(DataPath other) {
        if (this == other) return true;
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
