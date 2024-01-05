import java.io.IOException;

public class HeavyweightThread implements Runnable {
    private final HeavyweightProcess process;

    public HeavyweightThread(int processID, boolean initialToken, int[] lwPorts, int hwPort) throws IOException {
        process = new HeavyweightProcess(processID, initialToken, lwPorts, hwPort);
    }

    @Override
    public void run() {
        try {
            process.heavyweight();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
