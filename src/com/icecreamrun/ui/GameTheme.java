package com.icecreamrun.ui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

public final class GameTheme {
    public static final Color SKY_TOP = new Color(178, 217, 242);
    public static final Color SKY_BOTTOM = new Color(241, 251, 255);
    public static final Color PANEL_BLUE = new Color(214, 240, 252);
    public static final Color PANEL_BORDER = new Color(110, 178, 213);
    public static final Color ICE_BLUE = new Color(143, 220, 244);
    public static final Color ICE_DARK = new Color(53, 133, 170);
    public static final Color TEXT_DARK = new Color(38, 73, 96);
    public static final Color BUTTON_TOP = new Color(255, 255, 255);
    public static final Color BUTTON_BOTTOM = new Color(185, 231, 248);

    private static final String FONT_NAME = "Microsoft YaHei";

    private GameTheme() {
    }

    public static Font font(int size, int style) {
        return new Font(FONT_NAME, style, size);
    }

    public static void styleButton(JButton button) {
        button.setFont(font(16, Font.BOLD));
        button.setForeground(TEXT_DARK);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 18, 8, 18));
        button.setBackground(new Color(231, 248, 255));
        button.setOpaque(true);
    }

    public static void makeTransparent(JComponent component) {
        component.setOpaque(false);
    }
}
