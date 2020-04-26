package cz.fi.muni.pa036.listennotify.api;

import cz.fi.muni.pa036.listennotify.api.event.BinaryEvent;
import cz.fi.muni.pa036.listennotify.api.event.TextEvent;
import com.google.gson.Gson;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LISTEN / NOTIFY client which is able to retrieve notifications for its user.
 */
public abstract class AbstractListenNotifyClient extends Thread implements ListenNotifyClient {

    /**
     * Return a raw string directly coming from a channel's payload. The expected
     * format is a JSON compliant string which we can deserialize into
     * {@link Event}.
     */
    protected abstract String nextRawJson(ChannelName name);

    /**
     * The same as {@link nextRawJson} but return a batch containing
     * {@code noElements} items.
     *
     * @throws IllegalArgumentException the backing queue has less than
     * {@code noElements} items in it.
     */
    protected abstract List<String> nextRawJson(ChannelName name, int noElements);

    private final Gson gson = new Gson();
    
    protected CrudClient crudClient;

    static protected enum TableName {
        BIN,
        TEXT;
    }
    
    static protected enum ChannelName {
        // Corresponds to any change (INSERT, UPDATE, DELETE) in the text table
        Q_EVENT,
        // COrrecsponds to any change in the bin table
        Q_EVENT_BIN;
    }

    public void setCrudClient(CrudClient client) {
        this.crudClient = client;
    }
    
    @Override
    public TextEvent nextText() {
        return gson.fromJson(nextRawJson(ChannelName.Q_EVENT), TextEvent.class);
    }

    @Override
    public List<TextEvent> nextText(int count) {
        return nextRawJson(ChannelName.Q_EVENT, count)
                .stream()
                .parallel()
                .map(raw -> gson.fromJson(raw, TextEvent.class))
                .collect(Collectors.toList());
    }

    @Override
    public BinaryEvent nextBinary() {
        return gson.fromJson(nextRawJson(ChannelName.Q_EVENT_BIN), BinaryEvent.class);
    }

    @Override
    public List<BinaryEvent> nextBinary(int count) {
        return nextRawJson(ChannelName.Q_EVENT_BIN, count)
                .stream()
                .parallel()
                .map(raw -> gson.fromJson(raw, BinaryEvent.class))
                .collect(Collectors.toList());
    }
}
