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
        // Set initial positions
        if (playerId == 0) {
            this.x = 100;
            this.y = 250;
        } else {
            this.x = 400;
            this.y = 250;
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(playerId);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("RESTART")) {
                    GameServer.resetGame();
                    GameServer.broadcastPositions();
                    continue;
                }

                String[] parts = message.split(",");
                x = Integer.parseInt(parts[1]);
                y = Integer.parseInt(parts[2]);

                if (GameServer.checkTreasureCollision(x, y)) {
                    incrementScore();
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

    public void incrementScore() {
        score++;
    }

    public void resetScore() {
        score = 0;
    }

    public void resetPosition() {
        if (playerId == 0) {
            x = 100;
            y = 250;
        } else {
            x = 400;
            y = 250;
        }
    }
}