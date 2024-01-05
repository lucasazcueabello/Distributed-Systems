import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class RAMutex extends Process implements LockWithSockets {
    int myTS;
    LamportClock clock;
    LinkedList<Integer> pendingQ;
    int numOkay = 0;
    private final int N = 3;
    private int myID;
    private Msg msg;
    public LwSockets sockets;

    public RAMutex(int myID) throws IOException {
        myTS = Integer.MAX_VALUE;
        clock = new LamportClock();
        pendingQ = new LinkedList<>();

        this.myID = myID;
        this.msg = new Msg(N-1);
        this.sockets = new LwSockets(6871, myID);
    }

    public void createSockets(){
        sockets.createSockets();
    }

    public synchronized void requestCS() throws InterruptedException {
        clock.tick();
        myTS = clock.getValue();
        String message = myTS + " " + this.myID + " request";
        this.msg.broadcastMsg(message, sockets);
        while(numOkay < N - 1) wait();
    }

    public synchronized void releaseCS() {
        myTS = Integer.MAX_VALUE;
        numOkay = 0;
        clock.tick();
        while(!pendingQ.isEmpty()) {
            int pid = pendingQ.remove();
            int socketPosition = pid;
            if(pid > this.myID) socketPosition--;
            String message = myTS + " " + this.myID + " okay";
            this.msg.sendMsg(this.sockets.clientSocket[socketPosition], message);
        }
    }

    public synchronized void handleMsg(int timestamp, int src, String tag) {
        clock.receiveAction(timestamp);
        if (tag.trim().equals("request")) {
            if((myTS == Integer.MAX_VALUE) || (timestamp < myTS) || ((timestamp == myTS) && (src < myID))) {
                int socketPosition = src;
                if(src > this.myID) socketPosition--;
                String message = clock.getValue() + " " + this.myID + " okay";
                this.msg.sendMsg(this.sockets.clientSocket[socketPosition], message);
            } else {
                pendingQ.add(src);
            }
        } else if (tag.trim().equals("okay")) {
            numOkay++;
            if (numOkay == N - 1) notify();
        }
    }

    @Override
    public int getID() {
        return this.myID;
    }

    @Override
    public int getNumOkays() {
        return this.numOkay;
    }

    @Override
    public LwSockets getSockets() {
        return sockets; // Provide a proper implementation
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public InputStream getErrorStream() {
        return null;
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }

    @Override
    public int exitValue() {
        return 0;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void lock() {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
