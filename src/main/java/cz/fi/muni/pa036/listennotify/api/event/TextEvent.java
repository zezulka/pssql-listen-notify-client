package cz.fi.muni.pa036.listennotify.api.event;

import cz.fi.muni.pa036.listennotify.api.data.TextDataRow;

/**
 * Syntactic shortcut.
 * @author Miloslav Zezulka
 */
public class TextEvent extends Event<TextDataRow> {
    
    public TextEvent(String table, EventType action, TextDataRow data) {
        super(table, action, data);
    }  
}
