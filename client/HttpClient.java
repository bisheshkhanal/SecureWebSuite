package client;

import java.io.*;
import java.net.Socket;

public class HttpClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            // Perform handshake
            if (!client.Authenticator.performHandshake(in, out)) {
                System.out.println("Handshake failed. Exiting.");
                return;
            }

            // Send HTTP GET request
            String request = "GET /index.html HTTP/1.1\r\n" +
                             "Host: " + HOST + "\r\n" +
                             "Connection: close\r\n\r\n";
            out.write(request);
            out.flush();

            // Read and display response
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
