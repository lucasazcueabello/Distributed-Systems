import java.io.IOException;
import java.util.Arrays;

public class LightweightProcess {

    private LockWithSockets mutex;
    private int myID;
    private final int N = 3;
    private boolean startFlag;
    private Utils utils;
    private Msg msg;

    public LightweightProcess(int myID, LockWithSockets mutex) throws IOException {
        this.mutex = mutex;
        this.myID = myID;
        startFlag = false;
        utils = new Utils();
        msg = new Msg(N-1);
    }
    public void lightweight() throws InterruptedException {
        this.mutex.createSockets();

        Thread clientHandlerThread = new Thread(new ClientHandler(this.mutex, N-1));
        clientHandlerThread.start();

        try{
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while(true){
            waitHeavyWeight();
            try{
                Thread.sleep(1000 * myID);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.mutex.requestCS();
            for (int i=0; i<10; i++){
                if (this.mutex instanceof LamportMutex)System.out.println("I'm lightweight process A" + this.myID);
                else System.out.println("I'm lightweight process B" + this.myID);
                Thread.sleep(1000);
            }
            System.out.println();
            this.mutex.releaseCS();
            notifyHeavyWeight();
        }

    }

    public void waitHeavyWeight() {
        while(!startFlag) listenHeavyWeight();
    }

    public void notifyHeavyWeight() {
        this.startFlag = false;
        this.msg.sendMsg(this.mutex.getSockets().hwSocket, "finish");
    }

    private void listenHeavyWeight() {
        try {
            // Read data from the client
            if(this.mutex.getSockets().hwSocketReceive.getInputStream().available() > 0) {
                String line = utils.readMessage(this.mutex.getSockets().hwSocketReceive);
                line = line.trim();
                if(line.equals("start")){
                    this.startFlag = true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
