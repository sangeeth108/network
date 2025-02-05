package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

public class GamePanel extends JPanel {
    private int playerId;
    private PrintWriter out;
    private int playerX, playerY, otherX, otherY;
    private int score, otherScore;
    private int treasureX, treasureY; // Treasure position

    public GamePanel(int playerId, PrintWriter out) {
        this.playerId = playerId;
        this.out = out;
        this.score = 0;
        this.otherScore = 0;

        // Player 0 starts on the left, Player 1 starts on the right
        if (playerId == 0) {
            playerX = 100; playerY = 250;
        } else {
            playerX = 400; playerY = 250;
        }

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                movePlayer(e);
            }
        });

        Timer timer = new Timer(16, e -> repaint()); // Refresh screen every 16ms
        timer.start();
    }

    private void movePlayer(KeyEvent e) {
        if (playerId == 0) { // Player 0 uses WASD
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> playerY -= 10;
                case KeyEvent.VK_S -> playerY += 10;
                case KeyEvent.VK_A -> playerX -= 10;
                case KeyEvent.VK_D -> playerX += 10;
            }
        } else { // Player 1 uses Arrow Keys
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> playerY -= 10;
                case KeyEvent.VK_DOWN -> playerY += 10;
                case KeyEvent.VK_LEFT -> playerX -= 10;
                case KeyEvent.VK_RIGHT -> playerX += 10;
            }
        }

        // Send updated position to server
        out.println(playerId + "," + playerX + "," + playerY);
    }

    public void updatePositions(String serverMessage) {
        String[] data = serverMessage.split(" ");
        for (String pos : data) {
            String[] parts = pos.split(",");
            int id = Integer.parseInt(parts[0]);

            if (id == -1) { // Treasure position update
                treasureX = Integer.parseInt(parts[1]);
                treasureY = Integer.parseInt(parts[2]);
            } else if (id == playerId) {
                playerX = Integer.parseInt(parts[1]);
                playerY = Integer.parseInt(parts[2]);
                score = Integer.parseInt(parts[3]);
            } else {
                otherX = Integer.parseInt(parts[1]);
                otherY = Integer.parseInt(parts[2]);
                otherScore = Integer.parseInt(parts[3]);
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the scoreboard
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Player 1: " + score, 20, 20);
        g.drawString("Player 2: " + otherScore, 350, 20);

        // Draw the players
        g.setColor(playerId == 0 ? Color.BLUE : Color.RED);
        g.fillRect(playerX, playerY, 20, 20);
        g.setColor(playerId == 0 ? Color.RED : Color.BLUE);
        g.fillRect(otherX, otherY, 20, 20);

        // Draw the treasure
        g.setColor(Color.YELLOW);
        g.fillOval(treasureX, treasureY, 15, 15);
    }
}
