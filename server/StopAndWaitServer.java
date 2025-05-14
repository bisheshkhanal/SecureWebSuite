package server;


import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class StopAndWaitServer {
    private static final int PORT = 9000;
    private static final int PACKET_SIZE = 1024;
    private static final String OUTPUT_FILE = "received_file.txt";

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT);
             FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {

            System.out.println("StopAndWaitServer is running on port " + PORT);

            byte[] buffer = new byte[PACKET_SIZE];
            int expectedSeq = 0;

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                byte[] data = packet.getData();
                int seqNum = data[0];

                if (seqNum == expectedSeq) {
                    int dataLength = packet.getLength() - 1;
                    fos.write(data, 1, dataLength);

                    // Send ACK
                    byte[] ack = {(byte) seqNum};
                    socket.send(new DatagramPacket(ack, ack.length, packet.getAddress(), packet.getPort()));

                    expectedSeq = 1 - expectedSeq; // Alternate between 0 and 1
                } else {
                    // Resend last ACK
                    byte[] ack = {(byte) (1 - expectedSeq)};
                    socket.send(new DatagramPacket(ack, ack.length, packet.getAddress(), packet.getPort()));
                }
            }

        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}