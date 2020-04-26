package cz.fi.muni.pa036.listennotify.api.data;

/**
 * Immutable object representing data row from a text db table.
 *
 * @author Miloslav Zezulka
 */
public class TextDataRow extends DataRow<String> {

    public TextDataRow(int id, String value) {
        super(id, value);
    }

    @Override
    public String toString() {
        return "DataRow{" + "id=" + id + ", message=" + value + '}';
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
