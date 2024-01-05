public class DirectClock {
    private int[] clock;
    int myId;

    public DirectClock(int numProc, int id) {
        this.myId = id;
        this.clock = new int[numProc];
        for (int i = 0; i < numProc; i++) this.clock[i] = 0;
    }

    public int getValue(int i) {
        return clock[i];
    }

    public void tick(){
        this.clock[myId]++;
    }

    public void sendAction() {
        tick();
    }

    public void receiveAction(int sender, int sentValue) {
        this.clock[sender] = Math.max(this.clock[sender], sentValue);
        this.clock[myId] = Math.max(this.clock[myId], sentValue) + 1;
    }

    public int[] getClock() {
        return clock;
    }
}
