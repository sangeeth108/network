package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private int playerId;
    private PrintWriter out;
    private int playerX, playerY, otherX, otherY;
    private int score, otherScore;
    private int treasureX, treasureY;
    private boolean gameOver = false;
    private int winnerId = -1;

    private static final int GRAVITY = 1;
    private static final int JUMP_STRENGTH = 15;
    private int velocityY = 0;
    private boolean isJumping = false;

    private List<Platform> platforms = new ArrayList<>();

    public GamePanel(int playerId, PrintWriter out) {
        this.playerId = playerId;
        this.out = out;
        this.score = 0;
        this.otherScore = 0;

        // Platforms matching server layout
        platforms.add(new Platform(50, 300, 120));
        platforms.add(new Platform(150, 250, 70));
        platforms.add(new Platform(250, 200, 80));
        platforms.add(new Platform(350, 150, 100));
        platforms.add(new Platform(200, 100, 110));

        if (playerId == 0) {
            playerX = 100;
            playerY = 250;
        } else {
            playerX = 400;
            playerY = 250;
        }

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        out.println("RESTART");
                    }
                } else {
                    movePlayer(e);
                }
            }
        });

        Timer timer = new Timer(16, e -> updateGame());
        timer.start();
    }

    private void movePlayer(KeyEvent e) {
        if (playerId == 0) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> jump();
                case KeyEvent.VK_A -> playerX = Math.max(0, playerX - 10);
                case KeyEvent.VK_D -> playerX = Math.min(460, playerX + 10);
            }
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> jump();
                case KeyEvent.VK_LEFT -> playerX = Math.max(0, playerX - 10);
                case KeyEvent.VK_RIGHT -> playerX = Math.min(460, playerX + 10);
            }
        }
        out.println(playerId + "," + playerX + "," + playerY);
    }

    private void jump() {
        if (!isJumping) {
            isJumping = true;
            velocityY = -JUMP_STRENGTH;
        }
    }

    private void updateGame() {
        velocityY += GRAVITY;
        playerY += velocityY;

        boolean onPlatform = false;
        for (Platform platform : platforms) {
            if (playerY + 20 >= platform.getY() &&
                    playerY + 20 <= platform.getY() + 5 &&
                    playerX + 20 > platform.getX() &&
                    playerX < platform.getX() + platform.getWidth()) {
                onPlatform = true;
                playerY = platform.getY() - 20;
                velocityY = 0;
                isJumping = false;
                break;
            }
        }

        if (!onPlatform && playerY >= 350) {
            playerY = 350;
            velocityY = 0;
            isJumping = false;
        }

        repaint();
    }

    public void updatePositions(String serverMessage) {
        gameOver = false;
        winnerId = -1;

        String[] data = serverMessage.split(" ");
        for (String pos : data) {
            String[] parts = pos.split(",");
            int id = Integer.parseInt(parts[0]);

            if (id == -1) {
                treasureX = Integer.parseInt(parts[1]);
                treasureY = Integer.parseInt(parts[2]);
            } else if (id == -2) {
                gameOver = true;
                winnerId = Integer.parseInt(parts[1]);
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

        // Draw scores
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Player 1: " + score, 20, 20);
        g.drawString("Player 2: " + otherScore, 350, 20);

        // Draw platforms
        g.setColor(Color.GRAY);
        for (Platform platform : platforms) {
            g.fillRect(platform.getX(), platform.getY(), platform.getWidth(), 5);
        }

        // Draw players
        g.setColor(playerId == 0 ? Color.BLUE : Color.RED);
        g.fillRect(playerX, playerY, 20, 20);
        g.setColor(playerId == 0 ? Color.RED : Color.BLUE);
        g.fillRect(otherX, otherY, 20, 20);

        // Draw treasure
        g.setColor(Color.YELLOW);
        g.fillOval(treasureX, treasureY, 15, 15);

        // Draw game over message
        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String message = "Player " + (winnerId + 1) + " wins! Press R to restart";
            g.drawString(message, 100, 200);
        }
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