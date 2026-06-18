package com.icecreamrun.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

public class StartPanel extends JPanel {
    public StartPanel(Runnable onStartGame, Runnable onShowHelp) {
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GameTheme.makeTransparent(centerPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel sloganLabel = new JLabel("边跑步边吃雪糕，小心脑冻！", SwingConstants.CENTER);
        sloganLabel.setFont(GameTheme.font(20, Font.BOLD));
        sloganLabel.setForeground(GameTheme.TEXT_DARK);
        centerPanel.add(sloganLabel, gbc);

        JLabel titleLabel = new JLabel("IceCream Run", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 44));
        titleLabel.setForeground(new Color(35, 113, 154));
        gbc.insets = new Insets(12, 0, 28, 0);
        centerPanel.add(titleLabel, gbc);

        JButton startButton = createMenuButton("开始跑步");
        startButton.addActionListener(event -> onStartGame.run());
        gbc.insets = new Insets(8, 0, 8, 0);
        centerPanel.add(startButton, gbc);

        JButton helpButton = createMenuButton("玩法说明");
        helpButton.addActionListener(event -> onShowHelp.run());
        centerPanel.add(helpButton, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, GameTheme.SKY_TOP, 0, getHeight(), GameTheme.SKY_BOTTOM));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(255, 255, 255, 160));
        g2.fillOval(-70, getHeight() - 145, 380, 110);
        g2.fillOval(getWidth() - 310, getHeight() - 130, 330, 95);
        g2.setColor(new Color(132, 208, 235, 110));
        g2.fillRoundRect(34, 34, getWidth() - 68, getHeight() - 68, 26, 26);
        g2.dispose();
        super.paintComponent(graphics);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(190, 44));
        GameTheme.styleButton(button);
        return button;
    }
}
