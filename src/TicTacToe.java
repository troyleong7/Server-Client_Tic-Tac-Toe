import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class TicTacToe extends JPanel implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Create a 2D array to represent the game board
    public char[][] board = new char[3][3];
    public char currentPlayer; 
    public JButton[][] buttons = new JButton[3][3];
    private boolean yourTurn;
    private boolean isGameOver;
    private Service server;
    private String username;

    public TicTacToe(Service server, String username) {
        // Initialize the game board and set up the GUI
    	this.username = username;
    	this.server = server;
        initializeBoard();
        initializeGUI();
        yourTurn = false;
        isGameOver = false;
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
        if (yourTurn && clickedButton.getText().equals("") && !isGameOver) {
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
            
            try {
				server.sendBoardState(username, board);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
            // Check if the game is over
            if (isGameOver) {
            	ClientGUI.announceWinner("Player " + currentPlayer + " wins! \n");
            }
        }
    }


    public void setTurn(boolean turn) {
    	yourTurn = turn;
    }
    
    public boolean getTurn() {
    	return yourTurn;
    }
    
    public void displayBoard(char[][] newBoard) {
    	for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = newBoard[i][j];
                if(board[i][j] != ' ')  {
                	buttons[i][j].setText(String.valueOf(board[i][j]));
                }
            }
    	}
    }
}