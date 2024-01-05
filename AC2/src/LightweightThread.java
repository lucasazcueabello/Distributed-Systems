import java.io.IOException;

public class LightweightThread implements Runnable {
    private LightweightProcess process;
    private LockWithSockets mutex;

    public LightweightThread(int threadID, boolean lamportMutexFlag) throws IOException {
        if(lamportMutexFlag) mutex = new LamportMutex(threadID);
        else mutex = new RAMutex(threadID);

        this.process = new LightweightProcess(threadID, mutex);
    }

    @Override
    public void run() {
        try {
            process.lightweight();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
