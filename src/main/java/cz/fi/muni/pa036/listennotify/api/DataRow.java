package cz.fi.muni.pa036.listennotify.api;

import java.util.Objects;

/**
 * A very simplified data row representation, truncated for large data if necessary.
 * @author mzezulka
 */
public final class DataRow {
    protected final int id;
    protected final String value;
    
    public DataRow(int id, String value) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(value);
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
