package com.icecreamrun.ui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

public class HelpPanel extends JPanel {
    public HelpPanel(Runnable onBackHome) {
        setLayout(new BorderLayout(20, 20));

        JTextArea helpText = new JTextArea(buildHelpText());
        helpText.setEditable(false);
        helpText.setFocusable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setFont(GameTheme.font(19, java.awt.Font.BOLD));
        helpText.setForeground(GameTheme.TEXT_DARK);
        helpText.setOpaque(false);
        helpText.setMargin(new Insets(50, 90, 24, 90));

        JButton backButton = new JButton("返回首页");
        GameTheme.styleButton(backButton);
        backButton.addActionListener(event -> onBackHome.run());

        JPanel bottomPanel = new JPanel();
        GameTheme.makeTransparent(bottomPanel);
        bottomPanel.add(backButton);

        add(helpText, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, GameTheme.SKY_TOP, 0, getHeight(), GameTheme.SKY_BOTTOM));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(255, 255, 255, 145));
        g2.fillRoundRect(58, 42, getWidth() - 116, getHeight() - 112, 24, 24);
        g2.setColor(new Color(95, 174, 209, 130));
        g2.drawRoundRect(58, 42, getWidth() - 116, getHeight() - 112, 24, 24);
        g2.dispose();
        super.paintComponent(graphics);
    }

    private String buildHelpText() {
        return "玩法说明\n\n"
                + "操作：按空格键或鼠标左键跳跃。\n\n"
                + "香蕉皮在地上，跳起来可以躲避。\n"
                + "踩到 3 次香蕉皮，挑战失败。\n\n"
                + "雪糕在空中，跳起来可以吃到。\n"
                + "吃到雪糕会短暂加速，并增加脑冻值。\n\n"
                + "脑冻值满 100 会失败，跑到 1000 米就胜利。";
    }
}
