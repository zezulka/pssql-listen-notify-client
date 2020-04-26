package cz.fi.muni.pa036.listennotify.api.data;

import java.util.Objects;

public abstract class DataRow<T> {
    protected final int id;
    protected final T value;
    
    public DataRow(int id, T value) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(value);
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public T getMessage() {
        return value;
    }
}
