import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BlockingClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server. Kirim Pesan :");

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                String serverResponse = in.readLine();
                System.out.println("Server says: " + serverResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
