import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToe extends JPanel implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Create a 2D array to represent the game board
    private char[][] board = new char[3][3];
    private char currentPlayer = 'O'; // Player O starts
    private JButton[][] buttons = new JButton[3][3];

    public TicTacToe() {
        // Initialize the game board and set up the GUI
        initializeBoard();
        initializeGUI();
    }

    private void initializeBoard() {
        // Initialize the game board with empty spaces
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    private void initializeGUI() {
        setSize(300, 300);
        setLayout(new GridLayout(3, 3));

        // Create buttons for the game board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
                buttons[i][j].addActionListener(this);
                add(buttons[i][j]);
            }
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle button clicks
        JButton clickedButton = (JButton) e.getSource();

        // Check if the clicked button is empty and it's a valid move
        if (clickedButton.getText().equals("") && !isGameOver()) {
            clickedButton.setText(String.valueOf(currentPlayer));
            int row = -1, col = -1;

            // Find the row and column of the clicked button
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j] == clickedButton) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }

            // Update the game board
            board[row][col] = currentPlayer;

            // Check if the game is over
            if (isGameOver()) {
            	ClientGUI.announceWinner("Player " + currentPlayer + " wins! \n");
            } else {
                // Switch to the other player
                currentPlayer = (currentPlayer == 'O') ? 'X' : 'O';
            }
        }
    }

    private boolean isGameOver() {
        // Check for a win condition
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return true; // Horizontal win
            }
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                return true; // Vertical win
            }
        }

        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return true; // Diagonal win (top-left to bottom-right)
        }

        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            return true; // Diagonal win (top-right to bottom-left)
        }

        // Check for a draw
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false; // There are still empty spaces
                }
            }
        }

        ClientGUI.announceWinner("It's a draw \n");
        return false;
    }

}