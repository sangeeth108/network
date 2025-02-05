package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static int treasureX, treasureY;

    public static void main(String[] args) {
        System.out.println("Game server started...");
        spawnNewTreasure();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (clients.size() < 2) { // Allow only two players
                Socket socket = serverSocket.accept();
                System.out.println("Player connected: " + socket);

                ClientHandler clientHandler = new ClientHandler(socket, clients.size());
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void broadcastPositions() {
        StringBuilder data = new StringBuilder();

        for (ClientHandler client : clients) {
            data.append(client.getPlayerId()).append(",")
                    .append(client.getX()).append(",")
                    .append(client.getY()).append(",")
                    .append(client.getScore()).append(" ");
        }

        data.append("-1,").append(treasureX).append(",").append(treasureY); // Send treasure position

        for (ClientHandler client : clients) {
            client.sendMessage(data.toString());
        }
    }

    public static synchronized void spawnNewTreasure() {
        treasureX = (int) (Math.random() * 400 + 50);
        treasureY = (int) (Math.random() * 300 + 50);
        broadcastPositions();
    }

    public static boolean checkTreasureCollision(int x, int y) {
        return Math.abs(x - treasureX) < 20 && Math.abs(y - treasureY) < 20;
    }
}
