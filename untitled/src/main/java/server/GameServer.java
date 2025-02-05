package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Game server started...");
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

    // Send updated player positions to all clients
    public static synchronized void broadcastPositions(String positionData) {
        for (ClientHandler client : clients) {
            client.sendMessage(positionData);
        }
    }
}
