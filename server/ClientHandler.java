package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        // Handle the client connection
        
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {

            // Perform the handshake with the client
            if (!Authenticator.performHandshake(in, out)) {
                System.out.println("Handshake failed. Closing connection.");
                clientSocket.close();
                return;
            }

            // Read the request line, the first line of which contains the HTTP method and path
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            System.out.println("Request: " + requestLine);
            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String filePath = tokens[1].substring(1); // Removing the leading '/'

            // This is a simple HTTP server, so we only support GET requests
            if (!method.equals("GET")) {

                sendResponse(out, 501, "Not Implemented", "Method not supported.");
                return;

            }

            // If no file path is provided, serve the default index.html
            if (filePath.isEmpty()) filePath = "index.html";

            // Check if the file exists and is not a directory
            File file = new File("test/" + filePath);
            if (file.exists() && !file.isDirectory()) {
                // Read the file content and send it as a response
                byte[] fileContent = Files.readAllBytes(Paths.get(file.getPath()));
                String header = "HTTP/1.1 200 OK\r\n" +
                        "Content-Length: " + fileContent.length + "\r\n" +
                        "Content-Type: " + Files.probeContentType(file.toPath()) + "\r\n" +
                        "Connection: close\r\n\r\n";
                out.write(header.getBytes());
                out.write(fileContent);

            } else {
                sendResponse(out, 404, "Not Found", "File not found.");
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    /**
     * Sends an HTTP response to the client.
     *
     * @param out        The output stream to send the response to.
     * @param statusCode The HTTP status code.
     * @param statusText The HTTP status text.
     * @param body       The body of the response.
     * @throws IOException If an I/O error occurs.
     */
    private void sendResponse(OutputStream out, int statusCode, String statusText, String body) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Connection: close\r\n\r\n" +
                body;
        out.write(response.getBytes());
    }
}