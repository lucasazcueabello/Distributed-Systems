import java.io.IOException;
import java.net.Socket;

public class CoreServer extends Server{
    public int numberInstructions = 0;

    public CoreServer(int mainPort, int[] ports, int[] nextLayerPort) throws IOException, InterruptedException {
        super(mainPort, ports, nextLayerPort);
    }

    protected void numberInstructionsHandler() throws InterruptedException {
        sendMsgToWeb(this.mainPort, this.fileHashMap);
        if (socketNode2Write == null) return;
        numberInstructions++;
        if (numberInstructions >= 10){
            msg.broadcastMsg(hashMapToString(fileHashMap),socketNode2Write);
            sleep(1000);
            numberInstructions = 0;
        }
    }

    protected void readHandler(){

        try {
            for (int i = 0; i < socketNode1Read.length; i++) {
                if (socketNode1Read[i].getInputStream().available() > 0) {
                    String line = msg.readMessage(socketNode1Read[i]);
                    line = line.trim();

                    //Check for Ackowledgment
                    if(line.equals("ACK")){
                        this.numberAck++;
                        if(numberAck == socketNode1Read.length) {
                            allowTransactions = true;
                            this.numberAck = 0;
                        }
                        continue;
                    }

                    String[] parts = line.split(",");
                    int[] numbers = new int[2];
                    // Check if the string was split into two parts
                    if (parts.length == 2) {
                        try {
                            numbers[0] = Integer.parseInt(parts[0].split(":")[1].trim());
                            numbers[1] = Integer.parseInt(parts[1].trim());
                            updateValueInMap(numbers[0], numbers[1]);
                            this.numberInstructionsHandler();
                            msg.sendMsg(socketNode1Write[i], "ACK");
                        } catch (NumberFormatException | InterruptedException e) {
                            System.out.println("Invalid number format");
                        }
                    } else {
                        System.out.println("Invalid input format");
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
