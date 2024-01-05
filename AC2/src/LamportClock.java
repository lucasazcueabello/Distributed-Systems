public class LamportClock {
    int clock;

    public LamportClock() {
        this.clock = 0;
    }

    public int getValue() {
        return clock;
    }

    public void tick(){
        this.clock++;
    }

    public void sendAction() {
        tick();
    }

    public void receiveAction(int receivedValue) {
        this.clock = Math.max(this.clock, receivedValue) + 1;
    }
}
