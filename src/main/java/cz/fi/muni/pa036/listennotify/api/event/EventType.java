package cz.fi.muni.pa036.listennotify.api.event;

/**
 *
 * Type of event action received from a db.
 * 
 * @author Miloslav Zezulka
 */
public enum EventType {
    INSERT_BINARY,
    INSERT_TEXT,
    UPDATE_BINARY,
    UPDATE_TEXT,
    DELETE_BINARY,
    DELETE_TEXT;
}