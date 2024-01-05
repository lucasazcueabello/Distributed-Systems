import java.io.IOException;
import java.net.Socket;

public class Layer1Server extends Server{
    long startTime;

    public Layer1Server(int mainPort, int[] ports, int[] nextLayerPort) throws IOException, InterruptedException {
        super(mainPort, ports, nextLayerPort);
        startTime = System.currentTimeMillis();
    }

    protected void readHandler(){

        try {

            for (Socket socket : socketNode1Read){
                if (socket.getInputStream().available() > 0) {
                    String line = msg.readMessage(socket);
                    this.fileHashMap = stringToHashMap(line);
                    saveHashMap(this.fileHashMap, Integer.toString(this.mainPort));
                    sendMsgToWeb(this.mainPort, this.fileHashMap);
                    System.out.println("message ack AB");
                }
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= 10000) {
                sendLayer2HashMap();
                startTime = System.currentTimeMillis();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendLayer2HashMap() throws IOException {
        if (socketNode2Write == null) return;
        for (Socket socket : socketNode2Write){
            msg.sendMsg(socket, hashMapToString(this.fileHashMap));
        }
    }
}
