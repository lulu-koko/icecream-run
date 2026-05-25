package com.icecreamrun.ui;

import com.icecreamrun.data.GameResult;
import com.icecreamrun.game.GameConfig;
import com.icecreamrun.game.GameController;
import com.icecreamrun.game.GameLoop;
import com.icecreamrun.game.GameState;
import com.icecreamrun.model.BananaPeel;
import com.icecreamrun.model.IceCreamItem;
import com.icecreamrun.model.Player;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.function.Consumer;

public class GamePanel extends JPanel {
    private final GameState gameState;
    private final GameController gameController;
    private final GameLoop gameLoop;
    private final Consumer<GameResult> onGameOver;
    private final RunwayPanel runwayPanel;
    private final JLabel bottomInfoLabel;
    private final BufferedImage[] playerRunImages;
    private final BufferedImage iceCreamImage;
    private final BufferedImage bananaImage;
    private boolean promptVisible;

    public GamePanel(GameState gameState, Runnable onBackHome, Consumer<GameResult> onGameOver) {
        this.gameState = gameState;
        this.gameController = new GameController(gameState);
        this.gameLoop = new GameLoop(this::updateGame);
        this.onGameOver = onGameOver;
        this.runwayPanel = new RunwayPanel();
        this.bottomInfoLabel = new JLabel();
        this.playerRunImages = loadRunImages();
        this.iceCreamImage = loadImage("/com/icecreamrun/assets/icecream.png");
        this.bananaImage = loadImage("/com/icecreamrun/assets/banana.png");

        setLayout(new BorderLayout());
        setFocusable(true);
        installInputHandlers();

        add(runwayPanel, BorderLayout.CENTER);
        add(createBottomPanel(onBackHome), BorderLayout.SOUTH);
        refreshBottomInfo();
    }

    public void startGame() {
        promptVisible = true;
        gameController.startNewGame();
        gameLoop.start();
        refreshBottomInfo();
        runwayPanel.repaint();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    public void stopGame() {
        gameController.stop();
        gameLoop.stop();
    }

    private void updateGame() {
        GameResult result = gameController.update(runwayPanel.getWidth(), runwayPanel.getGroundY());
        refreshBottomInfo();
        runwayPanel.repaint();

        if (result != null) {
            gameLoop.stop();
            onGameOver.accept(result);
        }
    }

    private void installInputHandlers() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "jump");
        getActionMap().put("jump", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                jump();
            }
        });

        MouseAdapter jumpByMouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                jump();
                requestFocusInWindow();
            }
        };
        addMouseListener(jumpByMouse);
        runwayPanel.addMouseListener(jumpByMouse);
    }

    private void jump() {
        promptVisible = false;
        gameController.jump();
        runwayPanel.repaint();
    }

    private JPanel createBottomPanel(Runnable onBackHome) {
        JPanel bottomPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(225, 248, 255),
                        0, getHeight(), new Color(184, 228, 246)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(93, 165, 202));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(900, 44));

        bottomInfoLabel.setFont(GameTheme.font(17, Font.BOLD));
        bottomInfoLabel.setForeground(GameTheme.TEXT_DARK);

        JButton backButton = new JButton("返回首页");
        GameTheme.styleButton(backButton);
        backButton.addActionListener(event -> onBackHome.run());

        JPanel infoWrap = new JPanel(new BorderLayout());
        GameTheme.makeTransparent(infoWrap);
        infoWrap.add(bottomInfoLabel, BorderLayout.CENTER);

        JPanel buttonWrap = new JPanel();
        GameTheme.makeTransparent(buttonWrap);
        buttonWrap.add(backButton);

        bottomPanel.add(infoWrap, BorderLayout.WEST);
        bottomPanel.add(buttonWrap, BorderLayout.EAST);
        return bottomPanel;
    }

    private void refreshBottomInfo() {
        bottomInfoLabel.setText("  用时：" + gameState.getElapsedSeconds()
                + " 秒    吃到的雪糕：" + gameState.getIceCreamCount()
                + " 个    香蕉皮：" + gameState.getBananaHits()
                + " / " + GameConfig.MAX_BANANA_HITS);
    }

    private BufferedImage loadImage(String resourcePath) {
        try {
            return ImageIO.read(getClass().getResource(resourcePath));
        } catch (IllegalArgumentException | IOException exception) {
            return null;
        }
    }

    private BufferedImage[] loadRunImages() {
        BufferedImage[] images = new BufferedImage[8];
        int loadedCount = 0;
        for (int i = 0; i < images.length; i++) {
            images[i] = loadImage("/com/icecreamrun/assets/player_run_" + i + ".png");
            if (images[i] != null) {
                loadedCount++;
            }
        }
        return loadedCount == images.length ? images : new BufferedImage[0];
    }

    private class RunwayPanel extends JPanel {
        private RunwayPanel() {
            setBackground(GameTheme.SKY_BOTTOM);
        }

        private int getGroundY() {
            return getHeight() - 116;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int groundY = getGroundY();

            drawBackground(g2, width, height, groundY);
            drawStatusBar(g2, width);
            drawPrompt(g2, width);
            drawGround(g2, width, groundY);
            drawItems(g2, groundY);
            drawPlayer(g2, groundY);

            g2.dispose();
        }

        private void drawBackground(Graphics2D g2, int width, int height, int groundY) {
            g2.setPaint(new GradientPaint(0, 0, GameTheme.SKY_TOP, 0, height, GameTheme.SKY_BOTTOM));
            g2.fillRect(0, 0, width, height);

            drawCloud(g2, 65, 180, 95, new Color(232, 248, 255, 180));
            drawCloud(g2, 315, 216, 120, new Color(226, 244, 253, 170));
            drawCloud(g2, 625, 205, 105, new Color(232, 248, 255, 185));

            drawMountain(g2, 22, groundY + 4, 92, 210);
            drawMountain(g2, 176, groundY + 12, 68, 155);
            drawMountain(g2, width - 165, groundY + 5, 88, 190);

            g2.setColor(new Color(201, 238, 250));
            g2.fillRoundRect(width / 2 + 120, groundY - 85, 170, 58, 18, 18);
            g2.setColor(new Color(121, 197, 224));
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(width / 2 + 135, groundY - 35, width / 2 + 255, groundY - 72);

            g2.setColor(new Color(255, 255, 255, 230));
            int[][] snow = {
                    {45, 74, 8}, {106, 40, 10}, {205, 132, 7}, {286, 70, 9},
                    {402, 108, 6}, {521, 154, 8}, {626, 96, 12}, {718, 128, 7},
                    {806, 84, 6}, {858, 160, 9}, {126, 196, 6}, {458, 202, 7}
            };
            for (int[] flake : snow) {
                g2.fillOval(flake[0] % Math.max(width, 1), flake[1], flake[2], flake[2]);
            }
        }

        private void drawMountain(Graphics2D g2, int x, int baseY, int width, int height) {
            Polygon mountain = new Polygon();
            mountain.addPoint(x, baseY);
            mountain.addPoint(x + width / 2, baseY - height);
            mountain.addPoint(x + width, baseY);

            g2.setColor(new Color(183, 223, 239));
            g2.fillPolygon(mountain);
            g2.setColor(Color.WHITE);
            Polygon snowCap = new Polygon();
            snowCap.addPoint(x + width / 2, baseY - height);
            snowCap.addPoint(x + width / 2 - width / 5, baseY - height + 58);
            snowCap.addPoint(x + width / 2, baseY - height + 44);
            snowCap.addPoint(x + width / 2 + width / 5, baseY - height + 58);
            g2.fillPolygon(snowCap);
            g2.setColor(new Color(97, 161, 190));
            g2.setStroke(new BasicStroke(2f));
            g2.drawPolyline(new int[]{x + 12, x + width / 2, x + width - 12},
                    new int[]{baseY - 18, baseY - height + 18, baseY - 16}, 3);
        }

        private void drawCloud(Graphics2D g2, int x, int y, int size, Color color) {
            g2.setColor(color);
            g2.fillOval(x, y, size, size / 2);
            g2.fillOval(x + size / 4, y - size / 5, size / 2, size / 2);
            g2.fillOval(x + size / 2, y + 2, size / 2, size / 3);
        }

        private void drawStatusBar(Graphics2D g2, int width) {
            g2.setColor(new Color(88, 139, 173, 92));
            g2.fillRoundRect(8, 8, width - 16, 58, 12, 12);
            drawOutlinedText(g2, "进度：" + gameState.getDistanceMeters()
                    + " / " + GameConfig.TARGET_DISTANCE_METERS + " m", 24, 44, 20);
            String brainText = "脑冻值：" + gameState.getBrainFreeze()
                    + " / " + GameConfig.MAX_BRAIN_FREEZE;
            int textWidth = g2.getFontMetrics(GameTheme.font(20, Font.BOLD)).stringWidth(brainText);
            drawOutlinedText(g2, brainText, width - textWidth - 26, 44, 20);
        }

        private void drawPrompt(Graphics2D g2, int width) {
            if (!promptVisible) {
                return;
            }
            String text = "按空格键或鼠标左键跳跃";
            g2.setFont(GameTheme.font(20, Font.BOLD));
            int textWidth = g2.getFontMetrics().stringWidth(text);
            drawOutlinedText(g2, text, (width - textWidth) / 2, 158, 20);
        }

        private void drawGround(Graphics2D g2, int width, int groundY) {
            g2.setColor(new Color(235, 251, 255));
            g2.fillRect(0, groundY - 54, width, 54);
            g2.setColor(new Color(255, 255, 255, 210));
            g2.fillOval(-60, groundY - 80, 340, 75);
            g2.fillOval(475, groundY - 76, 360, 70);

            int brickWidth = 58;
            int brickHeight = 42;
            for (int row = 0; row < 3; row++) {
                int y = groundY + row * brickHeight;
                int offset = row % 2 == 0 ? 0 : -brickWidth / 2;
                for (int x = offset; x < width + brickWidth; x += brickWidth) {
                    drawIceBrick(g2, x, y, brickWidth, brickHeight);
                }
            }

            g2.setColor(new Color(56, 151, 190));
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(0, groundY, width, groundY);
        }

        private void drawIceBrick(Graphics2D g2, int x, int y, int width, int height) {
            g2.setPaint(new GradientPaint(x, y, new Color(205, 246, 255),
                    x, y + height, new Color(98, 200, 232)));
            g2.fillRoundRect(x + 2, y + 2, width - 4, height - 4, 10, 10);
            g2.setColor(new Color(47, 143, 181));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x + 2, y + 2, width - 4, height - 4, 10, 10);
            g2.setColor(new Color(255, 255, 255, 160));
            g2.drawLine(x + 12, y + 12, x + width - 16, y + 8);
        }

        private void drawItems(Graphics2D g2, int groundY) {
            for (BananaPeel bananaPeel : gameState.getBananaPeels()) {
                drawBanana(g2, bananaPeel.getBounds(groundY));
            }

            for (IceCreamItem iceCreamItem : gameState.getIceCreamItems()) {
                drawIceCream(g2, iceCreamItem.getBounds(groundY));
            }
        }

        private void drawBanana(Graphics2D g2, Rectangle bounds) {
            if (bananaImage != null) {
                int drawWidth = 62;
                int drawHeight = 44;
                int drawX = bounds.x - 10;
                int drawY = bounds.y + bounds.height - drawHeight + 4;
                g2.drawImage(bananaImage, drawX, drawY, drawWidth, drawHeight, null);
                return;
            }

            int x = bounds.x;
            int y = bounds.y;
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(128, 91, 35));
            g2.drawArc(x + 2, y - 16, bounds.width - 4, bounds.height + 28, 202, 138);
            g2.setColor(new Color(255, 222, 71));
            g2.drawArc(x + 2, y - 18, bounds.width - 4, bounds.height + 26, 202, 138);
            g2.drawLine(x + bounds.width / 2, y + 2, x + 7, y + bounds.height - 1);
            g2.drawLine(x + bounds.width / 2, y + 2, x + bounds.width - 7, y + bounds.height - 1);
            g2.setColor(new Color(111, 78, 34));
            g2.fillOval(x + bounds.width / 2 - 3, y, 7, 7);
        }

        private void drawIceCream(Graphics2D g2, Rectangle bounds) {
            if (iceCreamImage != null) {
                int drawWidth = 38;
                int drawHeight = 68;
                int drawX = bounds.x + (bounds.width - drawWidth) / 2;
                int drawY = bounds.y - 8;
                g2.drawImage(iceCreamImage, drawX, drawY, drawWidth, drawHeight, null);
                return;
            }

            int x = bounds.x;
            int y = bounds.y;
            g2.setColor(new Color(145, 83, 53));
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + 8, y + bounds.height - 7, x + 5, y + bounds.height + 10);
            g2.drawLine(x + bounds.width - 8, y + bounds.height - 7, x + bounds.width - 11, y + bounds.height + 10);
            g2.setColor(new Color(205, 88, 210));
            g2.fillRoundRect(x + 2, y + 2, bounds.width - 4, bounds.height - 7, 14, 14);
            g2.setColor(new Color(113, 45, 155));
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(x + 2, y + 2, bounds.width - 4, bounds.height - 7, 14, 14);
            g2.setColor(new Color(239, 165, 244, 180));
            g2.drawLine(x + bounds.width / 2, y + 8, x + bounds.width / 2 - 8, y + bounds.height - 16);
        }

        private void drawPlayer(Graphics2D g2, int groundY) {
            Player player = gameState.getPlayer();
            int x = player.getX();
            int y = groundY - player.getHeight() + player.getYOffset();

            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillOval(x - 4, groundY - 7, player.getWidth() + 12, 12);

            if (playerRunImages.length > 0) {
                int frameIndex = (int) ((System.currentTimeMillis() / 90) % playerRunImages.length);
                BufferedImage frame = playerRunImages[frameIndex];
                int drawWidth = 78;
                int drawHeight = 105;
                int drawX = x - 18;
                int drawY = groundY - drawHeight + player.getYOffset() + 8;
                g2.drawImage(frame, drawX, drawY, drawWidth, drawHeight, null);
                return;
            }

            g2.setColor(new Color(68, 184, 142));
            g2.fillOval(x, y + 17, player.getWidth() + 8, player.getHeight() - 10);
            g2.setColor(new Color(33, 116, 93));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(x, y + 17, player.getWidth() + 8, player.getHeight() - 10);

            g2.setColor(new Color(112, 215, 173));
            g2.fillOval(x + 8, y + 6, 39, 39);
            g2.setColor(new Color(33, 116, 93));
            g2.drawOval(x + 8, y + 6, 39, 39);

            g2.setColor(Color.WHITE);
            g2.fillOval(x + 28, y + 16, 12, 15);
            g2.setColor(new Color(41, 72, 82));
            g2.fillOval(x + 31, y + 20, 6, 7);
            g2.setStroke(new BasicStroke(2f));
            g2.drawArc(x + 20, y + 30, 15, 10, 205, 110);

            g2.setColor(new Color(255, 174, 92));
            g2.fillOval(x + 6, y + 14, 9, 16);
            g2.fillOval(x + 42, y + 14, 8, 14);
            g2.setColor(new Color(91, 68, 44));
            g2.drawOval(x + 6, y + 14, 9, 16);
            g2.drawOval(x + 42, y + 14, 8, 14);

            g2.setColor(new Color(22, 103, 79));
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + 8, y + 50, x - 4, y + 45);
            g2.drawLine(x + 26, y + 72, x + 18, y + 80);
            g2.drawLine(x + 42, y + 72, x + 53, y + 79);
        }

        private void drawOutlinedText(Graphics2D g2, String text, int x, int y, int size) {
            g2.setFont(GameTheme.font(size, Font.BOLD));
            g2.setColor(new Color(255, 255, 255, 220));
            g2.drawString(text, x - 1, y);
            g2.drawString(text, x + 1, y);
            g2.drawString(text, x, y - 1);
            g2.drawString(text, x, y + 1);
            g2.setColor(new Color(20, 45, 62));
            g2.drawString(text, x, y);
        }
    }
}
