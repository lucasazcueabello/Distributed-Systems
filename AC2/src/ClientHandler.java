import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class ClientHandler implements Runnable {
    private LockWithSockets mutex;
    private final int num_clients;
    private Utils utils;

    public ClientHandler(LockWithSockets mutex, int num_clients) {
        this.mutex = mutex;
        this.num_clients = num_clients;
        this.utils = new Utils();
    }


    @Override
    public void run() {
        while (true) {
            this.readSocket();
        }
    }

    public void readSocket(){
        try {
            // Read data from the client
            for(int i = 0; i < num_clients; i++){
                if(this.mutex.getSockets().serverSocketReceive[i].getInputStream().available() > 0) {
                    String line = utils.readMessage(this.mutex.getSockets().serverSocketReceive[i]);
                    String[] parts = line.split(" ");
                    this.mutex.handleMsg(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2]);
                }
            }
            // Close the socket
            //clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}