package server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int playerId;

    public ClientHandler(Socket socket, int playerId) {
        this.socket = socket;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(playerId); // Send player ID to client

            String message;
            while ((message = in.readLine()) != null) {
                GameServer.broadcastPositions(message); // Send positions to all players
            }
        } catch (IOException e) {
            System.out.println("Player " + playerId + " disconnected.");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
