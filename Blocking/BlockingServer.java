import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BlockingServer {
    private static final int PORT = 8080;
    private static List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(out);

                Thread clientHandler = new Thread(new ClientHandler(clientSocket, out));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, PrintWriter out) throws IOException {
            this.clientSocket = socket;
            this.out = out;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String input;
                while ((input = in.readLine()) != null) {
                    System.out.println("Received from client: " + input);
                    for (PrintWriter client : clients) {
                        client.println("Client " + clientSocket + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + clientSocket);
                clients.remove(out);
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
