package aspnet.signalr;

/**
 * Created by Volodymyr Kusenko on 8/2/2018.
 */
public interface WebSocketClientListener {

    public void onOpen();

    public void onMessage(String message);

    public void onClose(int code, String reason, boolean remote);

    public void onError(Exception ex);
}
