package server;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class Authenticator {

    private static final String SHARED_SECRET = "supersecurekey123"; // This must match the client's authenticator

    public static boolean performHandshake(BufferedReader in, OutputStream out) throws IOException {

        // Generate a random challenge
        String nonce = UUID.randomUUID().toString();
        out.write(("Challenge: " + nonce + "\n").getBytes());
        out.flush();

        // Read client's response
        String response = in.readLine();
        if (response == null || response.isEmpty()) return false;

        String expectedDigest = computeSHA256(nonce + SHARED_SECRET);
        return response.trim().equals(expectedDigest);
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
