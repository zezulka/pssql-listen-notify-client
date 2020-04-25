package cz.fi.muni.pa036.listennotify.api;

import com.google.gson.Gson;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * LISTEN / NOTIFY client which implements some of the default behaviours of its
 * "engine".
 */
public abstract class AbstractListenNotifyClient implements ListenNotifyClient {

    private final Gson gson = new Gson();
    private final Map<EventType, PreparedStatement> stmtCache = new HashMap<>();
    private static final String TEXT_TABLE_NAME = "text";
    private static final String BINARY_TABLE_NAME = "binary";
    private static final String INSERT_STMT = "INSERT INTO %s VALUES (?, ?);";
    private static final String DELETE_STMT = "DELETE FROM %s WHERE id=?";
    private static final String UPDATE_STMT = "UPDATE %s SET %s = ? WHERE id=?";
    private boolean preparedStmtsReady = false;
    
    protected abstract Statement createStatement();
    protected abstract PreparedStatement createPreparedStatement(String query);

    /**
     * Return a raw string directly coming from the database. The expected
     * format is a JSON compliant string which we can deserialize into
     * {@link ListenNotifyEvent}.
     */
    protected abstract String nextRawJson();

    @Override
    public ListenNotifyEvent next() {
        return gson.fromJson(nextRawJson(), ListenNotifyEvent.class);
    }

    @Override
    public void executeStatement(String sqlStmt) throws SQLException {
        try ( Statement statement = createStatement()) {
            statement.execute(sqlStmt);
        }
    }

    private void createPreparedStatementsIfNecessary() {
        if(preparedStmtsReady) {
            return;
        }
        // prepared statements for the text table
        stmtCache.put(EventType.INSERT_TEXT, createPreparedStatement(String.format(INSERT_STMT, TEXT_TABLE_NAME)));
        stmtCache.put(EventType.DELETE_TEXT, createPreparedStatement(String.format(DELETE_STMT, TEXT_TABLE_NAME)));
        stmtCache.put(EventType.UPDATE_TEXT, createPreparedStatement(String.format(UPDATE_STMT, TEXT_TABLE_NAME, "message")));
        
        // prepared statements for the binary table
        stmtCache.put(EventType.INSERT_BINARY, createPreparedStatement(String.format(INSERT_STMT, BINARY_TABLE_NAME)));
        stmtCache.put(EventType.DELETE_BINARY, createPreparedStatement(String.format(DELETE_STMT, BINARY_TABLE_NAME)));
        stmtCache.put(EventType.UPDATE_BINARY, createPreparedStatement(String.format(UPDATE_STMT, BINARY_TABLE_NAME, "img")));
        
        preparedStmtsReady = true;
    }
    
    @Override
    public void insertText(int id, String text) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.INSERT_TEXT);
        ps.setInt(1, id);
        ps.setString(2, text);
        ps.executeUpdate();
    }
    
    @Override
    public void insertBinary(int id, FileInputStream fis) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.INSERT_BINARY);
        ps.setInt(1, id);
        ps.setBinaryStream(2, fis);
        ps.executeUpdate();
    }
    
    @Override
    public void deleteText(int id) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.DELETE_TEXT);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
    
    @Override
    public void deleteBinary(int id) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.DELETE_BINARY);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
    
    @Override
    public void updateText(int id, String text) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.UPDATE_TEXT);
        ps.setString(1, text);
        ps.setInt(2, id);
        ps.executeUpdate();
    }
    
    @Override
    public void updateBinary(int id, FileInputStream fis) throws SQLException {
        createPreparedStatementsIfNecessary();
        PreparedStatement ps = stmtCache.get(EventType.UPDATE_BINARY);
        ps.setBinaryStream(1, fis);
        ps.setInt(2, id);
        ps.executeUpdate();
    }

    @Override
    public void registerEventListener(EventType type) {
        try {
            executeStatement("LISTEN q_event");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deregisterEventListener(EventType type) {
        try {
            executeStatement("UNLISTEN q_event");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
