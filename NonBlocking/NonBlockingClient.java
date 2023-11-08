import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NonBlockingClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8181;

    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));
            socketChannel.configureBlocking(false);

            System.out.println("Connected to server. Type your message:");

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String userInput = stdIn.readLine();
                if (userInput == null || userInput.equalsIgnoreCase("exit")) {
                    break;
                }

                buffer.put(userInput.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();

                // Handle server response if needed...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
