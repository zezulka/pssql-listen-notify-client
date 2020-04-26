package cz.fi.muni.pa036.listennotify.api;

import java.util.Objects;

/**
 * Immutable object representing data row from a binary db table.
 * We usually store an image as a stream of bytes.
 *
 * @author Miloslav Zezulka
 */
public class BinaryDataRow extends DataRow {
    private final int id;
    private final byte[] img;
    
    public BinaryDataRow(int id, byte[] img) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(img);
        this.id = id;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public byte[] getImg() {
        return img;
    }
    
    @Override
    public String toString() {
        return "DataRow{" + "id=" + id + '}';
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
        final BinaryDataRow other = (BinaryDataRow) obj;
        return this.id == other.id;
    }
}
