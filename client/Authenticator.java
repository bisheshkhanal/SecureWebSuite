package client;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Authenticator {
    private static final String SHARED_SECRET = "supersecurekey123"; // Must match server's authenticator

    public static boolean performHandshake(BufferedReader in, BufferedWriter out) throws IOException {
        // Read the challenge line from the server
        String challengeLine = in.readLine();
        if (challengeLine == null || !challengeLine.startsWith("Challenge: ")) {
            System.out.println("Invalid challenge from server.");
            return false;
        }

        String nonce = challengeLine.substring("Challenge: ".length()).trim();
        String digest = computeSHA256(nonce + SHARED_SECRET);

        // Send the digest as a response
        out.write(digest);
        out.newLine();
        out.flush();

        return true; // You can wait for an explicit ACK from the server later if needed
    }

    private static String computeSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }
}
