package tictactoe.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TicTacToeGUI extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private GameBoard gameBoard = new GameBoard();
    private char currentPlayer, aiPlayer, humanPlayer;
    private GameMode gameMode;
    private Color backgroundColor;

    public TicTacToeGUI() {
        showSettingsDialog();
    }

    private void showSettingsDialog() {
        JDialog dialog = new JDialog(this, "Game Settings", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(null);

        JComboBox<String> modeBox = new JComboBox<>(new String[]{"Pass and Play", "Play vs AI"});
        JComboBox<String> symbolBox = new JComboBox<>(new String[]{"X", "O"});
        JButton colorBtn = new JButton("Choose Background Color");
        JLabel colorLabel = new JLabel("No color selected");

        final Color[] selectedColor = {Color.WHITE};
        colorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(dialog, "Choose Background", Color.WHITE);
            if (chosen != null) {
                selectedColor[0] = chosen;
                colorLabel.setText("Color Selected");
            }
        });

        JButton startBtn = new JButton("Start Game");
        startBtn.addActionListener(e -> {
            gameMode = modeBox.getSelectedIndex() == 0 ? GameMode.PASS_AND_PLAY : GameMode.VS_AI;
            humanPlayer = symbolBox.getSelectedItem().equals("X") ? 'X' : 'O';
            aiPlayer = (humanPlayer == 'X') ? 'O' : 'X';
            currentPlayer = 'X';
            backgroundColor = selectedColor[0];
            dialog.dispose();
            initializeGame();
        });

        dialog.add(new JLabel("Select Mode:"));
        dialog.add(modeBox);
        dialog.add(new JLabel("Select Symbol:"));
        dialog.add(symbolBox);
        dialog.add(colorBtn);
        dialog.add(colorLabel);
        dialog.add(new JLabel(""));
        dialog.add(startBtn);

        dialog.setVisible(true);
    }

    private void initializeGame() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new GridLayout(3, 3));

        getContentPane().removeAll();

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("");
                btn.setFont(new Font("Arial", Font.BOLD, 60));
                btn.setBackground(backgroundColor);
                final int row = i, col = j;
                btn.addActionListener(e -> handleMove(row, col));
                buttons[i][j] = btn;
                add(btn);
            }

        gameBoard.resetBoard();
        revalidate();
        repaint();
        setVisible(true);
    }

    private void handleMove(int row, int col) {
        if (!gameBoard.makeMove(row, col, currentPlayer)) return;

        buttons[row][col].setText(String.valueOf(currentPlayer));
        buttons[row][col].setEnabled(false);

        if (GameLogic.checkWin(gameBoard.getBoard(), currentPlayer)) {
            showMessage(currentPlayer + " wins!");
            return;
        } else if (GameLogic.isBoardFull(gameBoard.getBoard())) {
            showMessage("It's a draw!");
            return;
        }

        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';

        if (gameMode == GameMode.VS_AI && currentPlayer == aiPlayer) {
            aiMove();
        }
    }

    private void aiMove() {
        int[] move = GameLogic.getBestMove(gameBoard.getBoard(), aiPlayer, humanPlayer);
        if (move != null) {
            handleMove(move[0], move[1]);
        }
    }

    private void showMessage(String message) {
        int option = JOptionPane.showConfirmDialog(this, message + "\nPlay again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            showSettingsDialog();
        } else {
            dispose();
        }
    }
}
