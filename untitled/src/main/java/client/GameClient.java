package client;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class GameClient {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            int playerId = Integer.parseInt(in.readLine());
            System.out.println("Connected as Player " + playerId);

            JFrame frame = new JFrame("Networking Game");
            GamePanel panel = new GamePanel(playerId, out);
            frame.add(panel);
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                panel.updatePositions(serverMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}