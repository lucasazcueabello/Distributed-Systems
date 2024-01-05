import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HeavyweightProcess {
    private int myID;
    private boolean token;
    private int NUM_LIGHTWEIGHTS = 3;

    private final int[] lwPorts;
    private final int hwPort;
    private final String HOSTNAME = "127.0.0.1";
    public ServerSocket serverSocket;
    public Socket[] lwSocketReceive;
    private Socket[] lwSocket;
    public Socket hwSocketReceive;
    private Socket hwSocket;
    private int answersfromLightweigth;
    private Utils utils;
    private Msg msg;


    public HeavyweightProcess(int myID, boolean initialToken, int[] lwPorts, int hwPort) throws IOException {
        this.myID = myID;
        this.token = initialToken;
        this.lwPorts = lwPorts;
        this.utils = new Utils();
        this.msg = new Msg();

        this.hwPort = hwPort;
        this.lwSocket = new Socket[lwPorts.length];
        this.lwSocketReceive = new Socket[lwPorts.length];

        //Create server socket
        this.serverSocket = new ServerSocket(this.lwPorts[0] - 1);
    }

    public void createSockets(){
        try {
            //Connect to other sockets
            this.hwSocket = new Socket(this.HOSTNAME, this.hwPort);
            this.hwSocketReceive = this.serverSocket.accept();

            Thread.sleep(3000);
            for(int i = 0; i < NUM_LIGHTWEIGHTS; i++){
                this.lwSocketReceive[i] = this.serverSocket.accept();
            }
            for(int i = 0; i < NUM_LIGHTWEIGHTS; i++){
                this.lwSocket[i] = new Socket(this.HOSTNAME, this.lwPorts[i]);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void heavyweight() throws InterruptedException {

        this.createSockets();

        while(true){
            while(!token) listenHeavyweight();
            for (int i = 0; i < NUM_LIGHTWEIGHTS; i++) {
                sendActionToLightweight(i);
            }
            while(answersfromLightweigth < NUM_LIGHTWEIGHTS) {
                listenLightweight();
            }
            token = false;
            sendTokenToHeavyweight();
        }
    }

    //Remove after testing
    public void heavyweightTest() throws InterruptedException {
        try{

            //Connect to other sockets
            this.hwSocket = new Socket(this.HOSTNAME, this.hwPort);

            this.hwSocketReceive = this.serverSocket.accept();
        } catch(IOException e){
            throw new RuntimeException(e);
        }

        //this.utils.sendMsg(hwSocket, "HW to HW");

        while(true){
            while(!token) listenHeavyweight();
            Thread.sleep(3000);
            token = false;
            sendTokenToHeavyweight();
        }
    }

    public void listenHeavyweight() {
        try {
            // Read data from the client
            if(this.hwSocketReceive.getInputStream().available() > 0) {
                String line = utils.readMessage(hwSocketReceive);
                line = line.trim();
                if(line.equals("token")){
                    token = true;
                    //System.out.println("Token received from HW");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenLightweight() {
        try {
            for(int i = 0; i < NUM_LIGHTWEIGHTS; i++){
                // Read data from the client
                if(this.lwSocketReceive[i].getInputStream().available() > 0) {
                    String line = utils.readMessage(lwSocketReceive[i]);
                    line = line.trim();
                    if(line.equals("finish")) {
                        this.answersfromLightweigth++;
                        //System.out.println("LW finished");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTokenToHeavyweight() {
        this.answersfromLightweigth = 0;
        this.msg.sendMsg(hwSocket, "token");
        //System.out.println("Send token to heavyweight");
    }

    public void sendActionToLightweight(int num_socket) {
        this.msg.sendMsg(lwSocket[num_socket], "start");
        //System.out.println("Start lw " + num_socket);
    }
}
