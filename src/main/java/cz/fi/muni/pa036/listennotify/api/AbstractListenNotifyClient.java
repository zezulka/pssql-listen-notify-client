package cz.fi.muni.pa036.listennotify.api;

import com.google.gson.Gson;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LISTEN / NOTIFY client which is able to retrieve notifications for its user.
 */
public abstract class AbstractListenNotifyClient extends Thread implements ListenNotifyClient {

    /**
     * Return a raw string directly coming from the database. The expected
     * format is a JSON compliant string which we can deserialize into
     * {@link Event}.
     */
    protected abstract String nextRawJson(TableName name);
    
    /**
     * The same as {@link nextRawJson} but return a batch containing {@code noElements} items.
     * @throws IllegalArgumentException the backing queue has less than {@code noElements} items in it.
     */
    protected abstract List<String> nextRawJson(TableName name, int noElements);

    private final Gson gson = new Gson();
    
    @Override
    public TextEvent nextText() {
        return gson.fromJson(nextRawJson(TableName.TEXT), TextEvent.class);
    }
    
    @Override
    public List<TextEvent> nextText(int count) {
        return nextRawJson(TableName.TEXT, count)
                .stream()
                .parallel()
                .map(raw -> gson.fromJson(raw, TextEvent.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public BinaryEvent nextBinary() {
        return gson.fromJson(nextRawJson(TableName.BIN), BinaryEvent.class);
    }
    
    @Override
    public List<BinaryEvent> nextBinary(int count) {
        return nextRawJson(TableName.BIN, count)
                .stream()
                .parallel()
                .map(raw -> gson.fromJson(raw, BinaryEvent.class))
                .collect(Collectors.toList());
    }
}
