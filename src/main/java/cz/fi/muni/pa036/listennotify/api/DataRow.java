package cz.fi.muni.pa036.listennotify.api;

import java.util.Objects;

/**
 * Immutable object representing data row from a db table.
 *
 * @author Miloslav Zezulka
 */
public class DataRow {
    
    private final int id;
    private final int domainid;
    private final String command;
    
    public DataRow(int id, int domainid, String command) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(domainid);
        Objects.requireNonNull(command);
        this.id = id;
        this.domainid = domainid;
        this.command = command;
    }
    
    @Override
    public String toString() {
        return "DataRow{" + "id=" + id + ", domainid=" + domainid + ", command=" + command + '}';
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
        if (!(obj instanceof DataRow)) {
            return false;
        }
        final DataRow other = (DataRow) obj;
        return this.id == other.id;
    }
}
