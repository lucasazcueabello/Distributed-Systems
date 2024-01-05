import java.io.IOException;
import java.net.Socket;

public class Layer2Server extends Server{

    public Layer2Server(int mainPort, int[] ports, int[] nextLayerPort) throws IOException, InterruptedException {
        super(mainPort, ports, nextLayerPort);
    }

    protected void readHandler(){

        try {
            for (Socket socket : socketNode1Read){
                if (socket.getInputStream().available() > 0) {
                    String line = msg.readMessage(socket);
                    this.fileHashMap = stringToHashMap(line);
                    saveHashMap(this.fileHashMap, Integer.toString(this.mainPort));
                    sendMsgToWeb(this.mainPort, this.fileHashMap);
                    System.out.println("message ack BC");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
