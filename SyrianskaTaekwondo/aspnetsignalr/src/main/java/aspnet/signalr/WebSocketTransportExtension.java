package aspnet.signalr;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URISyntaxException;

/**
 * Created by Volodymyr Kusenko on 8/2/2018.
 */
public class WebSocketTransportExtension extends WebSocketTransport {

    private WebSocketClientListener listener;

    public WebSocketTransportExtension(String url, WebSocketClientListener listener) throws URISyntaxException {
        super(url);
        this.listener = listener;
    }

    @Override
    public WebSocketClient createWebSocket() {
        return new WebSocketClient(url) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                listener.onOpen();
            }

            @Override
            public void onMessage(String message) {
                listener.onMessage(message);
                try {
                    onReceive(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                listener.onClose(code, reason, remote);
            }

            @Override
            public void onError(Exception ex) {
                listener.onError(ex);
            }
        };
    }
}
