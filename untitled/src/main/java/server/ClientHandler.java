package server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int playerId;
    private int x, y, score;

    public ClientHandler(Socket socket, int playerId) {
        this.socket = socket;
        this.playerId = playerId;
        this.score = 0;
    }



    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(playerId); // Send player ID to client

            String message;
            while ((message = in.readLine()) != null) {
                String[] parts = message.split(",");
                x = Integer.parseInt(parts[1]);
                y = Integer.parseInt(parts[2]);

                if (GameServer.checkTreasureCollision(x, y)) {
                    score++;
                    GameServer.spawnNewTreasure();
                }

                GameServer.broadcastPositions();
            }
        } catch (IOException e) {
            System.out.println("Player " + playerId + " disconnected.");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }


    public int getPlayerId() {
        return playerId;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getScore() { return score; }
}
