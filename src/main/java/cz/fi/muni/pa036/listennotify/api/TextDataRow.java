package cz.fi.muni.pa036.listennotify.api;

import java.util.Objects;

/**
 * Immutable object representing data row from a text db table.
 *
 * @author Miloslav Zezulka
 */
public class TextDataRow extends DataRow {
    
    private final int id;
    private final String message;
    
    public TextDataRow(int id, String message) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(message);
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DataRow{" + "id=" + id + ", message=" + message + '}';
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.id;
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TextDataRow)) {
            return false;
        }
        final TextDataRow other = (TextDataRow) obj;
        return this.id == other.id;
    }
}
