package cz.fi.muni.pa036.listennotify.api.data;

/**
 * Immutable object representing data row from a binary db table.
 * We usually store an image as a stream of bytes.
 *
 * @author Miloslav Zezulka
 */
public class BinaryDataRow extends DataRow<byte[]> {

    public BinaryDataRow(int id, byte[] value) {
        super(id, value);
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
        if (!(obj instanceof BinaryDataRow)) {
            return false;
        }
        final BinaryDataRow other = (BinaryDataRow) obj;
        return this.id == other.id;
    }
}
