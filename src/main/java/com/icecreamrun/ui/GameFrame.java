package com.icecreamrun.ui;

import com.icecreamrun.data.GameResult;
import com.icecreamrun.game.GameState;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class GameFrame extends JFrame {
    private static final String START_PAGE = "start";
    private static final String HELP_PAGE = "help";
    private static final String GAME_PAGE = "game";
    private static final String RESULT_PAGE = "result";

    private final CardLayout cardLayout;
    private final JPanel pageContainer;
    private final GamePanel gamePanel;
    private final ResultPanel resultPanel;

    public GameFrame() {
        super("IceCream Run");

        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);
        gamePanel = new GamePanel(new GameState(), this::showStartPage, this::showResultPage);
        resultPanel = new ResultPanel(this::showGamePage, this::showStartPage);

        pageContainer.add(new StartPanel(this::showGamePage, this::showHelpPage), START_PAGE);
        pageContainer.add(new HelpPanel(this::showStartPage), HELP_PAGE);
        pageContainer.add(gamePanel, GAME_PAGE);
        pageContainer.add(resultPanel, RESULT_PAGE);

        setContentPane(pageContainer);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        showStartPage();
    }

    public void showStartPage() {
        gamePanel.stopGame();
        cardLayout.show(pageContainer, START_PAGE);
    }

    public void showHelpPage() {
        cardLayout.show(pageContainer, HELP_PAGE);
    }

    public void showGamePage() {
        cardLayout.show(pageContainer, GAME_PAGE);
        gamePanel.startGame();
    }

    public void showResultPage(GameResult result) {
        gamePanel.stopGame();
        resultPanel.setResult(result);
        cardLayout.show(pageContainer, RESULT_PAGE);
    }
}
