//Name: Yun Keng Leong	StudentID: 1133704

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

public class ClientGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static Timer timer;
	private static Timer crashTimer;
	private static Timer waitTimer;
	private static int countdown;
	private static int crashCount;
	private static int waitCount;
	private static JTextPane timerPane;
	private static ChatLog chatLog;
	private static JTextField messageField;
	private static JLabel turnLabel;
	private static TicTacToe tictactoe;
	private static JButton quitButton;
	public boolean turn;
	public boolean disTurn;
	public boolean wait;
	private String opponent;
	private int ranking;
	private int opponentRanking;
	String username;
	Service server;
	ClientFunction client;
	
	public ClientGUI(ClientFunction client, String username, Service server) throws RemoteException{
		this.client = client;
		this.server = server;
		this.username = username;
		turn = false;
		wait = false;
		initialize();
		
		// Chat window text field listerner
		messageField.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if(opponent != null && !wait) {
			    	chatLog.updateChat("Rank#" + ranking + " " + username + " : " + messageField.getText());
			    	try {
			    		server.sendMessage(client, messageField.getText());
			    		messageField.setText("");
			    	} catch (RemoteException e1) {
			    		System.out.println("removed");;
					}
		    	}
		    }
			});
		
		// Quit button listener
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
	            	if(!tictactoe.isGameOver && !(opponent == null)) {
						server.forfeitGame(client);
					}
	            	else {
	            		server.informOpponent(client);
	            	}
	            	server.unregister(client);
				} catch (RemoteException e1) {
					System.out.println("error at quit button");;
				}
				System.exit(0);
			}
		});
		
		// Window closing listener
		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	            try {
	            	if(!tictactoe.isGameOver  && !(opponent == null)) {
						server.forfeitGame(client);
					}
	            	else {
	            		server.informOpponent(client);
	            	}
	            	server.unregister(client);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
	            System.exit(0);
	        }
	    });
		
		//Turn timer count down listener
		timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	countdown--;
            	updateTimerDisplay();
                if (countdown < 0) {
                    timer.stop();
                }
                else if (countdown == 0) {
                    timer.stop();
                    tictactoe.playRandomMove();
                }
            }
        });
		
	}


	//Initialize the contents of the frame.
	private void initialize() {
		setBounds(100, 100, 670, 428);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JTextPane gameLabel = new JTextPane();
		gameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		gameLabel.setToolTipText("");
		gameLabel.setBackground(SystemColor.menu);
		gameLabel.setEditable(false);
		gameLabel.setText("Distributed Tic-Tac-Toe");
		gameLabel.setBounds(10, 189, 105, 59);
		getContentPane().add(gameLabel);
		
		quitButton = new JButton("QUIT");
		quitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		quitButton.setBounds(10, 285, 105, 23);
		getContentPane().add(quitButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(448, 34, 196, 312);
		getContentPane().add(scrollPane);
		
		chatLog = new ChatLog();
		scrollPane.setViewportView(chatLog);
		
		messageField = new JTextField();
		messageField.setBounds(448, 344, 196, 34);
		getContentPane().add(messageField);
		messageField.setColumns(10);
		
		JLabel chatLabel = new JLabel("Player Chat");
		chatLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		chatLabel.setHorizontalAlignment(SwingConstants.CENTER);
		chatLabel.setBounds(448, 11, 196, 23);
		getContentPane().add(chatLabel);
		
		timerPane = new JTextPane();
		timerPane.setEditable(false);
		timerPane.setBackground(SystemColor.menu);
		timerPane.setBounds(10, 34, 105, 105);
		
		StyledDocument doc = timerPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		getContentPane().add(timerPane);
		
		timerPane.setFont(new Font("Tahoma", Font.PLAIN, 25));
		timerPane.setText("Finding player");
		
		turnLabel = new JLabel("");
		turnLabel.setOpaque(true);
		turnLabel.setBackground(SystemColor.text);
		turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
		turnLabel.setBounds(137, 33, 301, 34);
		getContentPane().add(turnLabel);
		
		tictactoe = new TicTacToe(server, client);
		tictactoe.setBounds(10, 11, 280, 278);
		JPanel panel = new JPanel();
		panel.setBounds(138, 67, 300, 300);
		getContentPane().add(panel);
		panel.setLayout(null);
		panel.add(tictactoe);
		
		setVisible(true);
		
	}
	
	// Start the timer
	public void startTimer() {
		countdown = 20;
		timerPane.setFont(new Font("Tahoma", Font.PLAIN, 30));
		updateTimerDisplay();
        timer.start();
	}
	
	// Update timer display
	private static void updateTimerDisplay() {
        int seconds = countdown % 60;
        
        if(countdown <=0) {
        	timerPane.setFont(new Font("Tahoma", Font.PLAIN, 20));
    		timerPane.setText("Waiting for player turn");
    		if(tictactoe.isGameOver) {
    			timerPane.setText("Game ended" );
    		}
        }
        else {
        	String timeString = String.format("%02d", seconds);
        	timerPane.setText("Timer: \n" + timeString);
        }
    }
	
	// Announce winner in turn label
	public void announceWinner(String winner) {
		int winRank = 0;
		if(winner.equals(username)) {
			winRank = ranking;
		} else {
			winRank = opponentRanking;
		}
		turnLabel.setText("Player Rank#" + winRank + " " + winner + " wins!");
		tictactoe.GameOver();
		updateTimerDisplay();
	}

	//Show the message sent or received
	public void showMessage(String username, String message) {
		chatLog.updateChat("Rank#" + opponentRanking + " " + username + " : " + message);
	}
	
	// Check to see if its your turn and make the required action
	public void turn(boolean nextTurn) {
		turn = nextTurn;
		tictactoe.setTurn(nextTurn);
		if(turn) {
			turnLabel.setText("Rank#" + ranking + " " + username + "'s turn (" + tictactoe.currentPlayer + ")");
			startTimer();
		}
		else {
			char opponentSymb;
			countdown = -1;
			updateTimerDisplay();
			if(tictactoe.currentPlayer == 'O') {
				opponentSymb = 'X';
			}
			else {
				opponentSymb = 'O';
			}
			turnLabel.setText("Rank#" + opponentRanking + " " + opponent + "'s turn (" + opponentSymb + ")");
		}
	}
	
	// Get the opponent name
	public void getOpponent(String opponent) {
		this.opponent = opponent;
	}
	
	// Update the board
	public void updateBoard(char[][] board) {
		tictactoe.board = board;
		tictactoe.displayBoard(board);
	}
	
	// Get the player symbol
	public void getSymbol(char symb) {
		tictactoe.currentPlayer = symb;
	}
	
	// Announce match drawn in turn label
	public void announceDraw() {
		turnLabel.setText("Match drawn");
		tictactoe.GameOver();
		updateTimerDisplay();
	}
	
	// Show options after game end
	public void showOption() {
		JFrame frame = new JFrame();
		frame.setResizable(false);
		int x = this.getBounds().x  + (this.getBounds().width - 305)/2;
		int y = this.getBounds().y  + (this.getBounds().height - 130)/2;
		frame.setBounds(x, y, 305, 130);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel optionLabel = new JLabel("Would you want to find a new game or quit?");
		optionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		optionLabel.setBounds(10, 11, 268, 21);
		frame.getContentPane().add(optionLabel);
		
		JButton newButton = new JButton("New Game");
		newButton.setBounds(40, 43, 95, 23);
		frame.getContentPane().add(newButton);
		
		JButton qButton = new JButton("Quit");
		qButton.setBounds(145, 43, 95, 23);
		frame.getContentPane().add(qButton);
		
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				try {
					server.newGame(client);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		qButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					server.informOpponent(client);
					server.unregister(client);
				} catch (RemoteException e1) {
					System.out.println("removed");;
				}
				System.exit(0);
			}
		});
		
		
		frame.setVisible(true);
	}
	
	// Reset the GUI to initial state
	public void resetGUI() {
		wait = false;
		tictactoe.resetBoard();
		chatLog.resetChat();
		timerPane.setText("Finding player");
		turnLabel.setText("");
	}
	
	// Server crash panel 
	public void serverCrash() {
		JFrame crashFrame = new JFrame();
		crashCount = 5;
		crashFrame.setResizable(false);
		int x = this.getBounds().x  + (this.getBounds().width - 305)/2;
		int y = this.getBounds().y  + (this.getBounds().height - 130)/2;
		crashFrame.setBounds(x, y, 305, 130);
		crashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		crashFrame.getContentPane().setLayout(null);
		
		JLabel crashLabel = new JLabel("Server unavailable! Client closing in 5");
		crashLabel.setHorizontalAlignment(SwingConstants.CENTER);
		crashLabel.setBounds(10, 30, 268, 21);
		crashFrame.getContentPane().add(crashLabel);
		
		crashTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	crashCount--;
            	crashLabel.setText("Server unavailable! Client closing in " + crashCount);
                if (crashCount == 0) {
                    crashTimer.stop();
                    System.exit(0);
                }
            }
        });
		
		crashTimer.start();
		crashFrame.setVisible(true);
	}
	
	// Get player ranking
	public void getRanking(int rank) {
		this.ranking = rank;
	}
	
	// Get opponent ranking
	public void getOpponentRanking(int rank) {
		this.opponentRanking = rank;
		
	}
	
	// Wait for opponent to reconnect
	public void waitReconnect(boolean isTurn) {
		disTurn = isTurn;
		wait = true;
		JFrame waitFrame = new JFrame();
		waitCount = 30;
		waitFrame.setResizable(false);
		int x = this.getBounds().x  + (this.getBounds().width - 305)/2;
		int y = this.getBounds().y  + (this.getBounds().height - 130)/2;
		waitFrame.setBounds(x, y, 305, 130);
		waitFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		waitFrame.getContentPane().setLayout(null);
		
		JLabel waitLabel = new JLabel("Opponent Disconnected! Waiting reconnect: 30");
		waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
		waitLabel.setBounds(10, 30, 268, 21);
		waitFrame.getContentPane().add(waitLabel);
		
		waitTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	waitCount--;
            	if(wait) {
	            	waitLabel.setText("Opponent Disconnected! Waiting reconnect: " + waitCount);
	                if (waitCount == 0) {
	                    waitTimer.stop();
	                    try {
							server.drawGame(client, tictactoe.board);
							server.removeWaiting(opponent);
							waitFrame.dispose();
						} catch (RemoteException e1) {
							System.out.println("error in ClientGUI drawGame");
						}
	                }
            	}
            	else {
            		waitTimer.stop();
            		waitFrame.dispose();
            		try {
						server.reconnectBoardState(client, tictactoe.board, tictactoe.currentPlayer, disTurn);
					} catch (RemoteException e1) {
						System.out.println("error in ClientGUI sendBoard to reconnect");
					}
            	}
            }
        });
		
		waitTimer.start();
		waitFrame.setVisible(true);
	}
	
	// When disconnected opponent reconnected
	public void receiveReconnect() {
		wait = false;
	}
	
}
