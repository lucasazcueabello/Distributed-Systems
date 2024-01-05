import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client extends Server{
    private List<Transaction> transactions;
    public Client(int mainPort, int[] ports, int[] nextLayerPort) throws IOException, InterruptedException {
        super(mainPort, ports, nextLayerPort);
        this.transactions = readTextFile();
        System.out.println();
    }

    @Override
    public void behaviorSocket() {
        System.out.println();

        new Thread(() -> {
            while(true){
                for (Socket socket : socketNode1Read) {
                    try {
                        if (socket.getInputStream().available() > 0) {
                            String message = msg.readMessage(socket);
                            System.out.println("Client: " + message);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        for (Transaction transaction: transactions) {
            sendOperation(transaction);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void sendOperation(Transaction transaction){
        try {
            for (Socket socket: socketNode1Write) {
                if (transaction.port == socket.getPort()){
                    msg.sendMsg(socket, transaction.toString());
                    sleep(1000);
                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private List<Transaction> readTextFile() {
        try {
            FileReader reader = new FileReader("instructions.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            List<Transaction> transactionList = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                Transaction transaction = parseTransaction(line);
                transactionList.add(transaction);
            }

            reader.close();
            return transactionList;
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
        return null;
    }

    private Transaction parseTransaction(String input) {
        //Transaction transaction = new Transaction();

        String[] parts = input.split(", ");
        String base = parts[0];

        int[] coreLayerPorts = new int[]{
                Server.portClientsMap.get("A1"),
                Server.portClientsMap.get("A2"),
                Server.portClientsMap.get("A3")};
        int port = Utils.getRandomElement(coreLayerPorts);

        // Check if the base is read-only or not
        boolean isReadOnly = base.equals("b<0>") || base.equals("b<1>") || base.equals("b<2>");

        if (isReadOnly) {
            Matcher matcher = Pattern.compile("b<(\\d+)>").matcher(base);
            if (matcher.find()) {
                switch (matcher.group(1)){
                    case "0" -> {
                        port = Utils.getRandomElement(coreLayerPorts);
                    }
                    case "1" -> {
                        int[] ports = new int[]{
                                Server.portClientsMap.get("B1"),
                                Server.portClientsMap.get("B2")};
                        port = Utils.getRandomElement(ports);
                    }
                    case "2" -> {
                        int[] ports = new int[]{
                                Server.portClientsMap.get("C1"),
                                Server.portClientsMap.get("C2")};
                        port = Utils.getRandomElement(ports);
                    }
                }
            }
        }

        List<String> operationList = Arrays.asList(parts).subList(1, parts.length - 1);
        ArrayList<Operation> operations = new ArrayList<>();
        for (String operation : operationList) {
            Operation parsedOperation = Operation.parseOperation(operation);
            operations.add(parsedOperation);
        }
        return new Transaction(operations, port);

    }




}
