import java.io.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class LamportMutex extends Process implements LockWithSockets {
    private DirectClock clock;
    private int[] requestQ;
    private int numOkay;
    private int N = 3;
    private int myID;
    public LwSockets sockets;
    private Msg msg;

    public LamportMutex(int myID) throws IOException {

        clock = new DirectClock(N, myID);
        requestQ = new int[N];
        numOkay = 0;
        this.myID = myID; //Add process ID
        for (int i = 0; i < N; i++) {
            requestQ[i] = Integer.MAX_VALUE;
        }
        sockets = new LwSockets(6867, myID);
        msg = new Msg(N - 1);
    }

    public void createSockets(){
        sockets.createSockets();
    }

    public synchronized void requestCS() throws InterruptedException {
        clock.tick(); //Increase timer before sending event

        requestQ[myID] = clock.getValue(myID);
        String message = requestQ[myID] + " " + this.myID + " request";
        msg.broadcastMsg(message, this.sockets);
        while (!okayCS()) Thread.sleep(1000); //Thread.sleep(1000) or wait?

    }

    public synchronized void releaseCS() {
        clock.tick(); // Increase timer before sending event
        requestQ[myID] = Integer.MAX_VALUE;
        String message = clock.getValue(myID) + " " + this.myID + " release";
        msg.broadcastMsg(message, this.sockets);
        numOkay = 0;
    }


    private boolean okayCS() {

        if (numOkay < N - 1) return false;

        for (int i = 0; i < N; i++) {
            if (isGreater(requestQ[myID], myID, requestQ[i], i)) {
                return false;
            }
            if (isGreater(requestQ[myID], myID, clock.getValue(i), i)){
                return false;
            }
        }
        return true;
    }

    private boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
        if (entry2 == Integer.MAX_VALUE) return false;

        return ((entry1 > entry2) || ((entry1 == entry2) && (pid1) > pid2));
    }



    public void handleMsg(int timestamp, int src, String tag) {

        clock.receiveAction(src, timestamp); // Sync clocks (Increase timer in reception)

        tag = tag.trim();

        if (tag.equals("request")) {

            this.requestQ[src] = timestamp;

            int socketPosition = src;

            if(src > this.myID) socketPosition--;

            String message = clock.getValue(myID) + " " + this.myID + " ack";
            msg.sendMsg(this.sockets.clientSocket[socketPosition], message);

        } else if (tag.equals("ack")) {
            this.numOkay++;
        } else if (tag.equals("release")) {
            this.requestQ[src] = Integer.MAX_VALUE;
        }
    }

    public int getID() {
        return this.myID; // Provide a proper implementation
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

