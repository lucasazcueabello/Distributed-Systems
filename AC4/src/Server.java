import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Server extends Thread{

    private static final String HOSTNAME = "127.0.0.1";
    protected Socket[] socketNode1Read;
    protected Socket[] socketNode1Write;
    protected Socket[] socketNode2Read;
    protected Socket[] socketNode2Write;
    protected Socket[] clientSocket;
    protected ServerSocket serverSocket;

    protected int mainPort;
    protected int[] ports;
    private int[] nextLayerPort;

    protected Msg msg;
    protected static boolean allowTransactions;
    protected int numberAck;

    //public Utils utils;

    public static final HashMap<String, Integer> portClientsMap = new HashMap<>(){{
        put("A1", 6868);
        put("A2", 6869);
        put("A3", 6870);
        put("B1", 6871);
        put("B2", 6872);
        put("C1", 6873);
        put("C2", 6874);
        put("Client", 6880);
    }};

    public HashMap<Integer, Integer> fileHashMap = new HashMap<>(){{
        put(1, 76);
        put(2, 42);
        put(3, 18);
        put(4, 59);
        put(5, 87);
        put(6, 5);
        put(7, 33);
        put(8, 92);
        put(9, 12);
        put(10, 68);
        put(11, 41);
        put(12, 79);
        put(13, 23);
        put(14, 56);
        put(15, 37);
        put(16, 61);
        put(17, 14);
        put(18, 84);
        put(19, 49);
        put(20, 98);
        put(21, 72);
        put(22, 30);
        put(23, 81);
        put(24, 8);
        put(25, 64);
        put(26, 45);
        put(27, 99);
        put(28, 28);
        put(29, 54);
        put(30, 17);
        put(31, 76);
        put(32, 42);
        put(33, 18);
        put(34, 59);
        put(35, 87);
        put(36, 5);
        put(37, 33);
        put(38, 92);
        put(39, 12);
        put(40, 68);
        put(41, 41);
        put(42, 79);
        put(43, 23);
        put(44, 56);
        put(45, 37);
        put(46, 61);
        put(47, 14);
        put(48, 84);
        put(49, 49);
        put(50, 98);
        put(51, 72);
        put(52, 30);
        put(53, 81);
        put(54, 8);
        put(55, 64);
        put(56, 45);
        put(57, 99);
        put(58, 28);
        put(59, 54);
        put(60, 17);
        put(61, 76);
        put(62, 42);
        put(63, 18);
        put(64, 59);
        put(65, 87);
        put(66, 5);
        put(67, 33);
        put(68, 92);
        put(69, 12);
        put(70, 68);
        put(71, 41);
        put(72, 79);
        put(73, 23);
        put(74, 56);
        put(75, 37);
        put(76, 61);
        put(77, 14);
        put(78, 84);
        put(79, 49);
        put(80, 98);
        put(81, 72);
        put(82, 30);
        put(83, 81);
        put(84, 8);
        put(85, 64);
        put(86, 45);
        put(87, 99);
        put(88, 28);
        put(89, 54);
        put(90, 17);
        put(91, 76);
        put(92, 42);
        put(93, 18);
        put(94, 59);
        put(95, 87);
        put(96, 5);
        put(97, 33);
        put(98, 92);
        put(99, 12);
        put(100, 68);
    }};

    public enum LAYER_TYPE{
        CORE,LAYER1,LAYER2
    }

    public Server(int mainPort, int[] ports, int[] nextLayerPort) throws IOException, InterruptedException {
        //this.utils = new Utils();
        this.msg = new Msg();
        this.socketNode1Read = new Socket[ports.length];
        this.socketNode1Write = new Socket[ports.length];
        if (nextLayerPort!=null){
            this.socketNode2Write = new Socket[nextLayerPort.length];
            this.socketNode2Read = new Socket[nextLayerPort.length];
        }

        this.clientSocket = new Socket[2];
        this.serverSocket = new ServerSocket(mainPort);
        this.mainPort = mainPort;
        this.ports = ports;
        this.nextLayerPort = nextLayerPort;
        this.saveHashMap(this.fileHashMap, Integer.toString(this.mainPort));
        allowTransactions = true;
        numberAck = 0;
        sleep(1000);
    }

    protected void socketInitialization(Socket[] arraySockets, int[] portsArraySockets) throws IOException, InterruptedException {
        if (arraySockets == null) return;

        for (int i = 0; i < arraySockets.length; i++) {
            arraySockets[i] = new Socket(HOSTNAME, portsArraySockets[i]);
            msg.sendMsg(arraySockets[i], String.valueOf(this.mainPort));

        }
    }

    public void createSocket() throws IOException, InterruptedException {
        socketInitialization(socketNode1Write, ports);
        socketInitialization(socketNode2Write, nextLayerPort);
    }

    public void createSocketClient() throws IOException, InterruptedException {
        if (this.mainPort != portClientsMap.get("Client")){
            clientSocket[1] = new Socket(HOSTNAME, portClientsMap.get("Client"));
            msg.sendMsg(clientSocket[1], String.valueOf(this.mainPort));
        }
    }

    public void acceptSockets() throws IOException {

        int sum = 0;
        if (socketNode1Write != null){
            sum += socketNode1Write.length;
        }
        if (socketNode2Write != null){
            sum += socketNode2Write.length;
        }

        if (this.mainPort != portClientsMap.get("Client")) sum++;

        for (int numNodes = 0; numNodes < sum; numNodes++){

            Socket socket = serverSocket.accept();
            String message = msg.readMessage(socket);
            message = message.trim();
            int port = Integer.parseInt(message);

            for (int i = 0; i < socketNode1Read.length; i++) {
                int portSocket = socketNode1Write[i].getPort();
                if (port == portSocket) {
                    socketNode1Read[i] = socket;
                }
            }
            if (socketNode2Read != null){
                for (int i = 0; i < socketNode2Read.length; i++) {
                    int portSocket = socketNode2Write[i].getPort();
                    if (port == portSocket) {
                        socketNode2Read[i] = socket;
                    }
                }
            }

            if (port == portClientsMap.get("Client"))
                clientSocket[0] = socket;
        }

    }


    public int readSocketServers(){
        return 0;
    }

    protected void readHandler(){
    }

    protected void readHandler(long startTime){
    }

    @Override
    public void run() {
        behaviorSocket();
    }

    public void behaviorSocket(){
        long startTime = System.currentTimeMillis();

        while (true){
            try{
                if(clientSocket[0].getInputStream().available() > 0 && allowTransactions) {
                    String line = msg.readMessage(clientSocket[0]);
                    line = line.trim();
                    Transaction transaction = Transaction.parse(line);
                    clientHandler(transaction);
                }

                readHandler();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void clientHandler(Transaction transaction) throws InterruptedException {
        for (Operation operation:transaction.operations) {
            switch (operation.type){
                case "r"-> msg.sendMsg(clientSocket[1], String.valueOf(getValueFromMap(operation.number1)));
                case "w"->{
                    if (this instanceof CoreServer){
                        allowTransactions = false;
                        updateValueInMap(operation.number1, operation.number2);
                        sendMsgToWeb(this.mainPort, this.fileHashMap);
                        msg.broadcastMsg("Write:" + operation.number1 + "," + operation.number2, socketNode1Write);
                        ((CoreServer) this).numberInstructionsHandler();
                    }
                }
            }

            sleep(500);
        }
    }


    protected static class Operation {
        private String type;
        private int number1;
        private int number2 = -1;

        public Operation(String type, int number1, int number2) {
            this.type = type;
            this.number1 = number1;
            this.number2 = number2;
        }

        public Operation(String type, int number1) {
            this.type = type;
            this.number1 = number1;
        }

        @Override
        public String toString() {
            if (number2 != -1) {
                return String.format("%s(%d,%d)", type, number1, number2);
            } else {
                return String.format("%s(%d)", type, number1);
            }
        }

        protected static Operation parseOperation(String operation) {
            // Define a pattern for extracting type and number(s)
            Pattern pattern = Pattern.compile("(\\w)\\((\\d+)(?:,(\\d+))?\\)");
            Matcher matcher = pattern.matcher(operation);

            // Check if the pattern matches
            if (matcher.matches()) {
                String type = matcher.group(1);
                int number1 = Integer.parseInt(matcher.group(2));

                // Check if the second number exists
                if (matcher.group(3) != null)
                    return new Operation(type, number1, Integer.parseInt(matcher.group(3)));

                return new Operation(type, number1);
            } else {
                // Handle invalid operation format
                throw new IllegalArgumentException("Invalid operation format: " + operation);
            }
        }
    }

    protected static class Transaction {
        private ArrayList<Operation> operations;
        protected int port;

        public Transaction(ArrayList<Operation> operation, int port) {
            this.operations = operation;
            this.port = port;
        }


        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("Transaction [");
            for (Operation op : operations) {
                result.append(op.toString()).append(", ");
            }
            result.delete(result.length() - 2, result.length());  // Remove the trailing comma and space
            result.append("], Port: ").append(port);
            return result.toString();
        }

        public static Transaction parse(String transactionString){
            // Define a pattern for extracting operations and port
            Pattern pattern = Pattern.compile("Transaction \\[([^\\]]+)\\], Port: (\\d+)");
            Matcher matcher = pattern.matcher(transactionString);

            // Check if the pattern matches
            if (matcher.matches()) {
                ArrayList<Operation> operations = parseOperations(matcher.group(1));
                int port = Integer.parseInt(matcher.group(2));

                return new Transaction(operations, port);
            } else {
                // Handle invalid transaction format
                throw new IllegalArgumentException("Invalid transaction format: " + transactionString);
            }
        }

        private static ArrayList<Operation> parseOperations(String operationsString) {
            ArrayList<Operation> operations = new ArrayList<>();

            // Split the operationsString into individual operations
            String[] operationStrings = operationsString.split(", ");
            for (String operationString : operationStrings) {
                operations.add(Operation.parseOperation(operationString));
            }

            return operations;
        }

    }

    protected void updateValueInMap(int key, int value) {
        this.fileHashMap = readHashMap(Integer.toString(this.mainPort));
        this.fileHashMap.put(key, value);
        saveHashMap(this.fileHashMap, Integer.toString(this.mainPort));
    }

    protected int getValueFromMap(int key){
        this.fileHashMap = readHashMap(Integer.toString(this.mainPort));
        return this.fileHashMap.get(key);
    }

    protected HashMap<Integer, Integer> readHashMap(String file) {
        try (FileReader fileReader = new FileReader(file + ".json")) {
            Gson gson = new Gson();
            HashMap<Integer, Integer> map = gson.fromJson(fileReader, new TypeToken<HashMap<Integer, Integer>>() {}.getType());
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void saveHashMap(HashMap<Integer, Integer> map, String filename) {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File(filename + ".json");
        try {
            String stringMap = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(stringMap);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected HashMap<Integer, Integer> stringToHashMap(String string) {
        Gson gson = new Gson();
        return gson.fromJson(string, new TypeToken<HashMap<Integer, Integer>>() {}.getType());
    }

    protected String hashMapToString(HashMap<Integer, Integer> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String auxMapToString(HashMap<Integer, HashMap<Integer, Integer>> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void sendMsgToWeb(Integer port, HashMap<Integer, Integer> map) {
        HashMap<Integer, HashMap<Integer, Integer>> auxMap = new HashMap<>();
        auxMap.put(port, map);
        WebConnection.broadcastMessage(auxMapToString(auxMap));
    }
}
