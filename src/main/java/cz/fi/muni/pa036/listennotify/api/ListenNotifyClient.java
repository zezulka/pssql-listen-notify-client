package cz.fi.muni.pa036.listennotify.api;

import java.io.FileInputStream;
import java.util.List;
import java.sql.SQLException;

/**
 * Note: it is user's responsibility to ensure that ids are all valid (i.e.
 * when inserting, id is not already present in the table and when updating or
 * inserting, the id exists).
 * 
 * @author Miloslav Zezulka
 */
public interface ListenNotifyClient {

    /**
     * Return next event.
     */
    ListenNotifyEvent next();
    
    /**
     * Return next {@code noElements} events.
     * @throws IllegalArgumentException the backing queue has less than {@code noElements} items in it.
     */
    List<ListenNotifyEvent> next(int noelements);

    /**
     * Execute any arbitrary SQL statement. Use with care!
     *
     * @param sqlStmt SQL statement to be executed
     * @throws SQLException
     */
    void executeStatement(String sqlStmt) throws SQLException;

    /**
     * 
     * Insert text data into the database.
     * 
     * @param id id, must be unique in the database
     * @param text message to be stored in the table
     * @throws SQLException 
     */
    void insertText(int id, String text) throws SQLException;
    
        /**
     * 
     * Inserts binary stream (most usually an image) into the database.
     * 
     * @param id id, must be unique in the database
     * @param text file of the binary data to be stored
     * @throws SQLException 
     */
    void insertBinary(int id, FileInputStream text) throws SQLException;

    /**
     * 
     * Update text data with id {@code id}.
     * 
     * @param id id existing in the text table
     * @param text message to be stored in the table
     * @throws SQLException 
     */
    void updateText(int id, String text) throws SQLException;
    
        /**
     * 
     * Updates binary stream with id {@code id} into the database.
     * 
     * @param id id existing in the binary table
     * @param text file of the binary data to be stored
     * @throws SQLException 
     */
    void updateBinary(int id, FileInputStream text) throws SQLException;
    
    /**
     * 
     * Delete a row from the text table with id {@code id}. As a side effect,
     * if any row in the binary table references with its FK this id, it will
     * be deleted as well.
     * 
     * @param id id must exist in the database
     * @throws SQLException 
     */
    void deleteText(int id) throws SQLException;
    
    /**
     * Delete a row from the binary table with id {@code id}.
     * @param id
     * @throws SQLException 
     */
    void deleteBinary(int id) throws SQLException;
    
    /**
     * Inform db that the client wishes to accept events of type {@code type}
     * using the command LISTEN &lt;channel&gt;.
     *
     * @param type type of event the user wishes to listen to
     */
    void registerEventListener(EventType type);

    /**
     * Inform db that the client no longer wishes to accept events of type
     * {@code type}.
     *
     * @see
     * <a href="https://www.postgresql.org/docs/9.1/sql-unlisten.html">UNLISTEN
     * PostgreSQL documentation</a>
     * @param type type of event the user no longer wishes to listen to
     */
    void deregisterEventListener(EventType type);
}
