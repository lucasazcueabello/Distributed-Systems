import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LwSockets {
    private final int myID;
    private final int hwPort;
    private final String HOSTNAME = "127.0.0.1";
    private final int N = 2;
    public ServerSocket serverSocket;
    public Socket[] serverSocketReceive;
    public Socket hwSocketReceive;
    public Socket hwSocket;
    public Socket[] clientSocket;
    private Utils utils;

    public LwSockets(int hwPort, int myID) throws IOException {
        utils = new Utils();

        this.hwPort = hwPort;
        this.myID = myID;

        this.serverSocketReceive = new Socket[2];
        this.clientSocket = new Socket[2];

        //Create server socket
        this.serverSocket = new ServerSocket(this.hwPort + this.myID + 1);

    }

    public void createSockets() {
        //Create server socket
        try {
            //make sure heavyweight connects first between them
            this.hwSocket = new Socket(this.HOSTNAME, this.hwPort);
            this.hwSocketReceive = this.serverSocket.accept();

            Thread.sleep(3000);

            int socketValue = 0;
            //Connect to other sockets
            for (int i = 0; i < N; i++) {
                if (i == this.myID) socketValue++;
                this.clientSocket[i] = new Socket(this.HOSTNAME, this.hwPort + socketValue + 1);
                socketValue++;
            }

            Thread.sleep(1000 * this.myID);

            for (int i = 0; i < N; i++) {
                this.serverSocketReceive[i] = this.serverSocket.accept();
            }

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
