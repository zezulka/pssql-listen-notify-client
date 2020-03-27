package cz.fi.muni.pa036.listennotify.api;

import java.sql.SQLException;

/**
 *
 * @author Miloslav Zezulka
 */
public interface ListenNotifyClient {

    /**
     * Return next event of type {@link ListenNotifyEvent}.
     */
    ListenNotifyEvent next();

    /**
     * Execute any arbitrary SQL statement. Use with care!
     *
     * @param sqlStmt SQL statement to be executed
     * @throws SQLException
     */
    void executeStatement(String sqlStmt) throws SQLException;

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
