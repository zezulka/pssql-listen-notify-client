package cz.fi.muni.pa036.listennotify.api;

import cz.fi.muni.pa036.listennotify.api.event.EventType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
/**
 * Client which communicates with the database.
 *
 * @author mzezulka
 */
public abstract class CrudClient extends Thread {

    protected abstract Connection getConnection();

    protected abstract Statement createStatement();

    protected abstract PreparedStatement createPreparedStatement(String query);

    private final Map<EventType, PreparedStatement> stmtCache = new HashMap<>();
    private boolean preparedStmtsReady = false;
    private static final String TEXT_TABLE_NAME = "text";
    private static final String BINARY_TABLE_NAME = "bin";
    private static final String INSERT_STMT = "INSERT INTO %s VALUES (?, ?);";
    private static final String DELETE_STMT = "DELETE FROM %s WHERE id=?;";
    private static final String UPDATE_STMT = "UPDATE %s SET value = ? WHERE id=?;";

    /**
     * Execute any arbitrary SQL statement. Use with care!
     *
     * @param sqlStmt SQL statement to be executed
     * @throws SQLException
     */
    public void executeStatement(String sqlStmt) throws SQLException {
        try (Statement statement = createStatement()) {
            statement.execute(sqlStmt);
        }
    }

    /**
     * Insert a thousand rows using the COPY Postgre-specific command.
     */
    public void insertBulkText() throws SQLException {
        try {
            Long ci = new CopyManager((BaseConnection) getConnection())
                    .copyIn(
                            String.format("COPY text FROM STDIN (FORMAT csv, HEADER);"),
                            new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "data"
                                + File.separator + "batch.csv"))
                    );
            Logger.getGlobal().info(String.format("Inserted %d rows.", ci));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * Insert text data into the database.
     *
     * @param id id, must be unique in the database
     * @param text message to be stored in the table
     * @throws SQLException
     */
    public void insertText(int id, String text) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.INSERT_TEXT);
        ps.setInt(1, id);
        ps.setString(2, text);
        ps.executeUpdate();
    }

    /**
     *
     * Inserts binary stream (most usually an image) into the database.
     *
     * @param id id, must be unique in the database
     * @param fis file of the binary data to be stored
     * @throws SQLException
     */
    public void insertBinary(int id, FileInputStream fis) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.INSERT_BINARY);
        ps.setInt(1, id);
        ps.setBinaryStream(2, fis);
        ps.executeUpdate();
    }

    /**
     *
     * Update text data with id {@code id}.
     *
     * @param id id existing in the text table
     * @param text message to be stored in the table
     * @throws SQLException
     */
    public void updateText(int id, String text) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.UPDATE_TEXT);
        ps.setString(1, text);
        ps.setInt(2, id);
        ps.executeUpdate();
    }

    /**
     *
     * Updates binary stream with id {@code id} into the database.
     *
     * @param id id existing in the binary table
     * @param fis file of the binary data to be stored
     * @throws SQLException
     */
    public void updateBinary(int id, FileInputStream fis) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.UPDATE_BINARY);
        ps.setBinaryStream(1, fis);
        ps.setInt(2, id);
        ps.executeUpdate();
    }

    /**
     *
     * Delete a row from the text table with id {@code id}. As a side effect, if
     * any row in the binary table references with its FK this id, it will be
     * deleted as well.
     *
     * @param id id must exist in the database
     * @throws SQLException
     */
    public void deleteText(int id) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.DELETE_TEXT);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    /**
     * Delete a row from the binary table with id {@code id}.
     *
     * @param id
     * @throws SQLException
     */
    public void deleteBinary(int id) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.DELETE_BINARY);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    /**
     * Inform db that the client wishes to accept events of type {@code type}
     * using the command LISTEN &lt;channel&gt;.
     *
     * @param type type of event the user wishes to listen to
     */
    public void registerEventListener(EventType type) {
        Logger.getGlobal().info("Registering event listening for " + type);
        try {
            if (type == EventType.DELETE_BINARY || type == EventType.INSERT_BINARY || type == EventType.UPDATE_BINARY) {
                executeStatement("LISTEN q_event_bin");
            } else {
                executeStatement("LISTEN q_event");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Inform db that the client no longer wishes to accept events of type
     * {@code type}.
     *
     * @see
     * <a href="https://www.postgresql.org/docs/9.1/sql-unlisten.html">UNLISTEN
     * PostgreSQL documentation</a>
     * @param type type of event the user no longer wishes to listen to
     */
    public void deregisterEventListener(EventType type) {
        try {
            if (type == EventType.DELETE_BINARY || type == EventType.INSERT_BINARY || type == EventType.UPDATE_BINARY) {
                executeStatement("UNLISTEN q_event_bin");
            } else {
                executeStatement("UNLISTEN q_event");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createPreparedStatementsIfNecessary() {
        if (preparedStmtsReady) {
            return;
        }
        // prepared statements for the text table
        stmtCache.put(EventType.INSERT_TEXT, createPreparedStatement(String.format(INSERT_STMT, TEXT_TABLE_NAME)));
        stmtCache.put(EventType.DELETE_TEXT, createPreparedStatement(String.format(DELETE_STMT, TEXT_TABLE_NAME)));
        stmtCache.put(EventType.UPDATE_TEXT, createPreparedStatement(String.format(UPDATE_STMT, TEXT_TABLE_NAME)));

        // prepared statements for the binary table
        stmtCache.put(EventType.INSERT_BINARY, createPreparedStatement(String.format(INSERT_STMT, BINARY_TABLE_NAME)));
        stmtCache.put(EventType.DELETE_BINARY, createPreparedStatement(String.format(DELETE_STMT, BINARY_TABLE_NAME)));
        stmtCache.put(EventType.UPDATE_BINARY, createPreparedStatement(String.format(UPDATE_STMT, BINARY_TABLE_NAME)));

        preparedStmtsReady = true;
    }
}
