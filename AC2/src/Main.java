import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int NUM_LIGHTWEIGHTS = 3;
        int NUM_HEAVYWEIGHTS = 2;
        int[][] lwPorts = {{6868, 6869, 6870},{6872, 6873, 6874}};
        int[] hwPorts = {6871, 6867};
        Thread[] lwThreadA = new Thread[3];
        Thread[] lwThreadB = new Thread[3];
        Thread[] hwThread = new Thread[2];

        for(int i = 0; i < NUM_HEAVYWEIGHTS; i++) hwThread[i] = new Thread(new HeavyweightThread(i, i == 0, lwPorts[i], hwPorts[i]));

        for(int i = 0; i < NUM_LIGHTWEIGHTS; i++) lwThreadA[i] = new Thread(new LightweightThread(i, true));
        for(int i = 0; i < NUM_LIGHTWEIGHTS; i++) lwThreadB[i] = new Thread(new LightweightThread(i, false));

        for(int i = 0; i < NUM_HEAVYWEIGHTS; i++) hwThread[i].start();

        try{
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i < NUM_LIGHTWEIGHTS; i++) lwThreadA[i].start();
        for(int i = 0; i < NUM_LIGHTWEIGHTS; i++) lwThreadB[i].start();


    }
}