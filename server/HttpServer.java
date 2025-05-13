package server;

//imports 
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HttpServer {
    private static final int PORT = 8080; // Can be changed to 443 for HTTPS if needed
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {

        // Create a thread pool for handling client connections when the server starts
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("HTTP Server started on port " + PORT);

            // Continuously accept client connections, and create a new thread for each concurrent connection
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}
