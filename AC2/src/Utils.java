import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Utils {

    public Utils(){}

    public String readMessage(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message = "";
        int charCode;

        try {
            while ((charCode = reader.read()) != -1) {
                char character = (char) charCode;

                if (character == '\n') {
                    break; // Exit the loop when a newline character is encountered
                }

                message += character;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

}
