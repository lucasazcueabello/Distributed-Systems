
import java.io.IOException;
import java.util.concurrent.locks.Lock;

public interface LockWithSockets extends Lock {

    default LwSockets getSockets() {
        return null; // Default implementation, can be overridden by implementing classes
    }

    default void requestCS() throws InterruptedException {}
    default void releaseCS() {}

    default void createSockets(){}
    default void handleMsg(int timestamp, int src, String tag) {}

    default int getID(){
        return 0;
    }

    default int getNumOkays(){
        return 0;
    }
}

