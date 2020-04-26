package cz.fi.muni.pa036.listennotify.api;

/**
 *
 * @author Miloslav Zezulka
 */
public class TextEvent extends Event<TextDataRow> {
    
    public TextEvent(String table, EventType action, TextDataRow data) {
        super(table, action, data);
    }  
}
