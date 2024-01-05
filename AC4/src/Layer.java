import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class Layer {
    private ArrayList<Server> serverList = new ArrayList<Server>();




    public Layer(Server.LAYER_TYPE layerType) throws IOException, InterruptedException {


        int mainPort;
        int[] ports;
        int[] nextLayerPorts;
        switch (layerType){
            case CORE->{

                mainPort = Server.portClientsMap.get("A1");
                ports = new int[]{Server.portClientsMap.get("A2"), Server.portClientsMap.get("A3")};
                serverList.add(new CoreServer(mainPort, ports, null));

                mainPort = Server.portClientsMap.get("A2");
                ports = new int[]{Server.portClientsMap.get("A1"), Server.portClientsMap.get("A3")};
                nextLayerPorts = new int[]{Server.portClientsMap.get("B1")};
                serverList.add(new CoreServer(mainPort, ports, nextLayerPorts));

                mainPort = Server.portClientsMap.get("A3");
                ports = new int[]{Server.portClientsMap.get("A1"), Server.portClientsMap.get("A2")};
                nextLayerPorts = new int[]{Server.portClientsMap.get("B2")};
                serverList.add(new CoreServer(mainPort, ports, nextLayerPorts));
            }
            case LAYER1 -> {

                mainPort = Server.portClientsMap.get("B1");
                ports = new int[]{Server.portClientsMap.get("A2")};
                serverList.add(new Layer1Server(mainPort, ports, null));

                mainPort = Server.portClientsMap.get("B2");
                ports = new int[]{Server.portClientsMap.get("A3")};
                nextLayerPorts = new int[]{Server.portClientsMap.get("C1"), Server.portClientsMap.get("C2")};
                serverList.add(new Layer1Server(mainPort, ports, nextLayerPorts));
            }
            case LAYER2 -> {
                mainPort = Server.portClientsMap.get("C1");
                ports = new int[]{Server.portClientsMap.get("B2")};
                serverList.add(new Layer2Server(mainPort, ports, null));

                mainPort = Server.portClientsMap.get("C2");
                ports = new int[]{Server.portClientsMap.get("B2")};
                serverList.add(new Layer2Server(mainPort, ports, null));
            }
        }

    }

    public void createSockets() throws IOException, InterruptedException {
        for (Server server: serverList) {
            server.createSocket();
        }
    }

    public void acceptSockets() throws IOException {
        for (Server server: serverList) {
            server.acceptSockets();
        }
    }

    public void startSockets() throws InterruptedException {
        for (Server server: serverList) {
            server.start();
            sleep(1000);
        }
    }

    public void createSocketClient() throws IOException, InterruptedException {
        for (Server server: serverList) {
            server.createSocketClient();
        }
    }
}
