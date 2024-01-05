import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) {


        System.out.println("Servers being created...");
        ArrayList<Layer> layers = new ArrayList<>();
        try {
            WebSocket.start();
            layers.add(new Layer(Server.LAYER_TYPE.CORE));
            layers.add(new Layer(Server.LAYER_TYPE.LAYER1));
            layers.add(new Layer(Server.LAYER_TYPE.LAYER2));
            sleep(1000);
            createSockets(layers);
        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Servers created!");
    }

    private static void createSockets(ArrayList<Layer> layers) throws IOException, InterruptedException {
        for (Layer layer: layers) {
            layer.createSockets();
        }

        Client client = initializeClient();

        sleep(2000);
        for (Layer layer: layers) {
            layer.acceptSockets();
        }


        sleep(2000);
        for (Layer layer: layers) {
            layer.createSocketClient();
        }

        client.acceptSockets();

        sleep(4000);
        for (Layer layer: layers) {
            layer.startSockets();
        }

        sleep(5000);
        client.start();
    }


    private static Client initializeClient() throws IOException, InterruptedException {


        int mainPort = Server.portClientsMap.get("Client");
        int[] ports = new int[]{
                Server.portClientsMap.get("A1"),
                Server.portClientsMap.get("A2"),
                Server.portClientsMap.get("A3"),
                Server.portClientsMap.get("B1"),
                Server.portClientsMap.get("B2"),
                Server.portClientsMap.get("C1"),
                Server.portClientsMap.get("C2")
        };
        Client client = new Client(mainPort, ports, null);
        sleep(2000);
        client.createSocket();
        return client;
    }
}