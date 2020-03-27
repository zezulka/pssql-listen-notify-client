package cz.fi.muni.pa036.listennotify.api;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * LISTEN / NOTIFY client which implements some of the default behaviours of its
 * "engine".
 */
public abstract class AbstractListenNotifyClient implements ListenNotifyClient {

    private Gson gson = new Gson();

    protected abstract Statement createStatement();

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
