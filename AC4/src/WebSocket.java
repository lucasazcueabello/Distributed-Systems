import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static spark.Spark.*;
public class WebSocket {
    public static void start() throws IOException, NoSuchAlgorithmException {
        webSocket("/web", WebConnection.class);

        // Set up HTTP routes
        get("/", (req, res) -> "Hello, WebSocket Server!");
    }
}
