package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345;
    private static final int WIN_SCORE = 5;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static int treasureX, treasureY;
    private static List<Platform> platforms = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Game server started...");
        spawnPlatforms();
        spawnNewTreasure();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (clients.size() < 2) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, clients.size());
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean checkTreasureCollision(int x, int y) {
        int treasureSize = 15;
        if (x < treasureX + treasureSize && x + 20 > treasureX &&
                y < treasureY + treasureSize && y + 20 > treasureY) {
            return true;
        }
        return false;
    }

    public static synchronized void broadcastPositions() {
        StringBuilder data = new StringBuilder();
        for (ClientHandler client : clients) {
            data.append(client.getPlayerId()).append(",")
                    .append(client.getX()).append(",")
                    .append(client.getY()).append(",")
                    .append(client.getScore()).append(" ");
        }
        data.append("-1,").append(treasureX).append(",").append(treasureY);

        // Check for winner
        for (ClientHandler client : clients) {
            if (client.getScore() >= WIN_SCORE) {
                data.append(" -2,").append(client.getPlayerId());
                break;
            }
        }

        for (ClientHandler client : clients) {
            client.sendMessage(data.toString());
        }
    }

    public static synchronized void spawnNewTreasure() {
        Platform platform = platforms.get(new Random().nextInt(platforms.size()));
        treasureX = platform.getX() + (int) (Math.random() * platform.getWidth());
        treasureY = platform.getY() - 15;
    }

    public static synchronized void spawnPlatforms() {
        // Easier platform layout
        platforms.add(new Platform(50, 300, 120));   // Bottom platform
        platforms.add(new Platform(150, 250, 70));  // Connected platform
        platforms.add(new Platform(250, 200, 80));  // Middle platform
        platforms.add(new Platform(350, 150, 100));  // Upper platform
        platforms.add(new Platform(200, 100, 110));  // Top platform
    }

    public static synchronized void resetGame() {
        for (ClientHandler client : clients) {
            client.resetPosition();
            client.resetScore();
        }
        spawnNewTreasure();
    }

    private static class Platform {
        private int x, y, width;
        public Platform(int x, int y, int width) {
            this.x = x;
            this.y = y;
            this.width = width;
        }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getWidth() { return width; }
    }
}