//Name: Yun Keng Leong	StudentID: 1133704

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public boolean isGameOver;
    public boolean isDraw;
    private Service server;
    private ClientFunction client;

    public TicTacToe(Service server, ClientFunction client) {
        // Initialize the game board and set up the GUI
    	this.client = client;
    	this.server = server;
        initializeBoard();
        initializeGUI();
        yourTurn = false;
        isGameOver = false;
        isDraw = false;
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
            afterAction();
        }
    }

    // Check winning condition after a move is made
    private void afterAction() {
    	try {
        	if(server.isGameOver(this) == 1) {
        		isGameOver = true;
        		isDraw = false;
        	}
        	else if(server.isGameOver(this) == 2) {
        		isGameOver = true;
        		isDraw = true;
        	}
		} catch (RemoteException e1) {
			System.out.println("Server crashed");
		}
        if (isGameOver && !isDraw) {
        	try {
				server.announceWinner(client, board);
			} catch (RemoteException e1) {
				System.out.println("Server crashed");
			}
        }else if(isGameOver && isDraw) {
        	try {
				server.drawGame(client, board);
			} catch (RemoteException e1) {
				System.out.println("Server crashed");
			}
        }else {
            try {
				server.sendBoardState(client, board);
			} catch (RemoteException e1) {
				System.out.println("Server crashed");
			}
        }
    }
    
    // Set the current player turn
    public void setTurn(boolean turn) {
    	yourTurn = turn;
    }
    
    // Display the current board state
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
    
    // Game over signal 
	public void GameOver() {
		isGameOver = true;
	}
	
	// Play random move
	public void playRandomMove() {
		List<List<Integer>> availableMove = new ArrayList<>();
		int row = -1, col =-1;
		for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            	List<Integer> move = new ArrayList<>();
                if (board[i][j] == ' ') {
                    move.add(i);
                    move.add(j);
                    availableMove.add(move);
                }
            }
        }
		Random random = new Random();
        int randomIndex = random.nextInt(availableMove.size());
		row = availableMove.get(randomIndex).get(0);
		col = availableMove.get(randomIndex).get(1);
		buttons[row][col].setText(String.valueOf(currentPlayer));
		board[row][col] = currentPlayer;
		afterAction();
	}
	
	// Reset the board to initial state
	public void resetBoard(){
		for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
                buttons[i][j].setText("");
            }
        }
		yourTurn = false;
        isGameOver = false;
        isDraw = false;
	}
}