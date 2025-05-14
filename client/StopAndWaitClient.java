package client;

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class StopAndWaitClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9000;
    private static final int PACKET_SIZE = 1024;
    private static final String INPUT_FILE = "send_this.txt";
    private static final int TIMEOUT_MS = 1000;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
             FileInputStream fis = new FileInputStream(INPUT_FILE)) {

            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            byte[] fileBuffer = new byte[PACKET_SIZE - 1];
            int seqNum = 0;
            int bytesRead;

            while ((bytesRead = fis.read(fileBuffer)) != -1) {
                byte[] packetData = new byte[bytesRead + 1];
                packetData[0] = (byte) seqNum;
                System.arraycopy(fileBuffer, 0, packetData, 1, bytesRead);

                DatagramPacket packet = new DatagramPacket(packetData, packetData.length, serverAddress, SERVER_PORT);

                while (true) {
                    socket.send(packet);

                    // Wait for ACK
                    byte[] ackBuf = new byte[1];
                    DatagramPacket ackPacket = new DatagramPacket(ackBuf, ackBuf.length);

                    try {
                        socket.receive(ackPacket);
                        if (ackBuf[0] == (byte) seqNum) {
                            break; // ACK received
                        }
                    } catch (Exception timeout) {
                        System.out.println("Timeout. Retrying packet with seq " + seqNum);
                    }
                }

                seqNum = 1 - seqNum; // Alternate sequence number
            }

            System.out.println("File transfer complete.");

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
