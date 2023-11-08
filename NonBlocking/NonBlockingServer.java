import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingServer {
    private static final int PORT = 8181;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started on port " + PORT);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("Client connected: " + clientChannel);
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                        int bytesRead = clientChannel.read(buffer);

                        if (bytesRead == -1) {
                            clientChannel.close();
                            System.out.println("Client disconnected: " + clientChannel);
                            break;
                        }

                        buffer.flip();
                        byte[] receivedData = new byte[buffer.remaining()];
                        buffer.get(receivedData);
                        String receivedMessage = new String(receivedData);
                        System.out.println("Received from client " + clientChannel + ": " + receivedMessage);

                        // Handle server response here (echo back to the client)
                        buffer.clear();
                        buffer.put(("Server received: " + receivedMessage).getBytes());
                        buffer.flip();
                        clientChannel.write(buffer);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
