package com.icecreamrun.ui;

import com.icecreamrun.data.FailReason;
import com.icecreamrun.data.GameResult;
import com.icecreamrun.data.GameResultType;
import com.icecreamrun.game.GameConfig;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

public class ResultPanel extends JPanel {
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel reasonLabel;
    private final JLabel distanceLabel;
    private final JLabel timeLabel;
    private final JLabel iceCreamLabel;
    private final JLabel bananaLabel;
    private final JLabel brainFreezeLabel;
    private final JLabel commentLabel;

    public ResultPanel(Runnable onRetry, Runnable onBackHome) {
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GameTheme.makeTransparent(contentPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(4, 0, 4, 0);

        titleLabel = createLabel(36, Font.BOLD);
        subtitleLabel = createLabel(30, Font.BOLD);
        reasonLabel = createLabel(17, Font.BOLD);
        distanceLabel = createLabel(17, Font.BOLD);
        timeLabel = createLabel(28, Font.BOLD);
        iceCreamLabel = createLabel(17, Font.BOLD);
        bananaLabel = createLabel(17, Font.BOLD);
        brainFreezeLabel = createLabel(17, Font.BOLD);
        commentLabel = createLabel(24, Font.BOLD);

        contentPanel.add(titleLabel, gbc);
        contentPanel.add(subtitleLabel, gbc);
        contentPanel.add(reasonLabel, gbc);
        contentPanel.add(distanceLabel, gbc);

        gbc.insets = new Insets(12, 0, 8, 0);
        contentPanel.add(timeLabel, gbc);

        gbc.insets = new Insets(4, 0, 4, 0);
        contentPanel.add(iceCreamLabel, gbc);
        contentPanel.add(bananaLabel, gbc);
        contentPanel.add(brainFreezeLabel, gbc);

        gbc.insets = new Insets(12, 0, 4, 0);
        contentPanel.add(commentLabel, gbc);

        JPanel buttonPanel = new JPanel();
        GameTheme.makeTransparent(buttonPanel);
        JButton retryButton = new JButton("再来一次");
        JButton homeButton = new JButton("返回首页");
        GameTheme.styleButton(retryButton);
        GameTheme.styleButton(homeButton);
        retryButton.addActionListener(event -> onRetry.run());
        homeButton.addActionListener(event -> onBackHome.run());
        buttonPanel.add(retryButton);
        buttonPanel.add(homeButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, GameTheme.SKY_TOP, 0, getHeight(), GameTheme.SKY_BOTTOM));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(255, 255, 255, 150));
        g2.fillRoundRect(92, 52, getWidth() - 184, getHeight() - 126, 24, 24);
        g2.setColor(new Color(84, 166, 205, 130));
        g2.drawRoundRect(92, 52, getWidth() - 184, getHeight() - 126, 24, 24);
        g2.dispose();
        super.paintComponent(graphics);
    }

    public void setResult(GameResult result) {
        if (result.getType() == GameResultType.WIN) {
            showWin(result);
        } else {
            showFail(result);
        }
    }

    private void showWin(GameResult result) {
        titleLabel.setText("你居然真的边跑边吃到终点了！");
        subtitleLabel.setText("1000 米挑战成功");
        reasonLabel.setText("");
        distanceLabel.setText("");
        timeLabel.setText("用时：" + result.getElapsedSeconds() + " 秒");
        iceCreamLabel.setText("吃到雪糕：" + result.getIceCreamCount() + " 个");
        bananaLabel.setText("踩到香蕉皮：" + result.getBananaHits()
                + " / " + GameConfig.MAX_BANANA_HITS + " 次");
        brainFreezeLabel.setText("最高脑冻值：" + result.getMaxBrainFreeze()
                + " / " + GameConfig.MAX_BRAIN_FREEZE);
        commentLabel.setText("高手，找作者领一根雪糕！");
    }

    private void showFail(GameResult result) {
        titleLabel.setText("你跑不过我你信不信？");
        subtitleLabel.setText("挑战失败");
        reasonLabel.setText("失败原因：" + getFailReasonText(result.getFailReason()));
        distanceLabel.setText("本次距离：" + result.getDistanceMeters()
                + " / " + GameConfig.TARGET_DISTANCE_METERS + " m");
        timeLabel.setText("用时：" + result.getElapsedSeconds() + " 秒");
        iceCreamLabel.setText("吃到雪糕：" + result.getIceCreamCount() + " 个");
        bananaLabel.setText("踩到香蕉皮：" + result.getBananaHits()
                + " / " + GameConfig.MAX_BANANA_HITS + " 次");
        brainFreezeLabel.setText("最高脑冻值：" + result.getMaxBrainFreeze()
                + " / " + GameConfig.MAX_BRAIN_FREEZE);
        commentLabel.setText(getFailComment(result.getFailReason()));
    }

    private String getFailReasonText(FailReason failReason) {
        if (failReason == FailReason.BANANA) {
            return "踩到 3 次香蕉皮";
        }
        if (failReason == FailReason.BRAIN_FREEZE) {
            return "雪糕吃太猛，脑子冻住了";
        }
        return "挑战未完成";
    }

    private String getFailComment(FailReason failReason) {
        if (failReason == FailReason.BANANA) {
            return "你和香蕉锁死吧！";
        }
        if (failReason == FailReason.BRAIN_FREEZE) {
            return "雪糕赢了，你输了！";
        }
        return "";
    }

    private JLabel createLabel(int size, int style) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(GameTheme.font(size, style));
        label.setForeground(GameTheme.TEXT_DARK);
        return label;
    }
}
