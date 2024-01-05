import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebConnection extends org.eclipse.jetty.websocket.api.WebSocketAdapter {
    private static final CopyOnWriteArrayList<WebConnection> connections = new CopyOnWriteArrayList<>();

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        connections.add(this);
        System.out.println("New connection: " + session.getRemoteAddress().getAddress());
    }

    @Override
    public void onWebSocketText(String message) {
        System.out.println("Received message: " + message);
        broadcastMessage(message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        connections.remove(this);
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace();
    }

    public static void broadcastMessage(String message) {
        for (WebConnection client : connections) {
            try {
                client.getSession().getRemote().sendString(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
