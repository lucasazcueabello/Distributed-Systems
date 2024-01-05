import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Msg {

    public void sendMsg(Socket clientsocket, String message) {
        try {
            for (int i = 0; i < message.length(); i++) {
                clientsocket.getOutputStream().write(message.charAt(i));
                //sleep(50);
            }
            clientsocket.getOutputStream().write('\n');

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void broadcastMsg(String message, Socket[] sockets) throws InterruptedException {
        for(int i = 0; i < sockets.length; i++) {
            sendMsg(sockets[i], message);
            sleep(500);
        }
    }

    public String readMessage(Socket socket) throws IOException {
        char readChar;
        StringBuilder message = new StringBuilder();
        do{
            readChar = (char) socket.getInputStream().read();
            message.append(readChar);
        }while(readChar!='\n');
        return message.substring(0, message.length()-1);
    }

}
