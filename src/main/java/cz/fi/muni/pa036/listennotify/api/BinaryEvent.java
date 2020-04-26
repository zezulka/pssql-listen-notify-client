package cz.fi.muni.pa036.listennotify.api;

/**
 * Syntactic shortcut.
 * @author Miloslav Zezulka
 */
public class BinaryEvent extends Event<BinaryDataRow> {
    
    public BinaryEvent(String table, EventType action, BinaryDataRow data) {
        super(table, action, data);
    }
}
