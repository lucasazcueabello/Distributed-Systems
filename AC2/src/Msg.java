import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Msg {
    private final int numMessages;
    public Msg(int numMessages){
        this.numMessages = numMessages;
    };
    public Msg(){
        this.numMessages = 0;
    };

    public void sendMsg(Socket clientsocket, String message) {
        try {
            PrintWriter writer = new PrintWriter(clientsocket.getOutputStream(),true);
            writer.println(message);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void broadcastMsg(String message, LwSockets sockets){
        for(int i = 0; i < this.numMessages; i++) sendMsg(sockets.clientSocket[i], message);
    }

}
