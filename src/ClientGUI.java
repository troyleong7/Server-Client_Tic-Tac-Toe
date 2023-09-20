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
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Timer timer;
	private static int countdown;
	private static JTextPane timerPane;
	private static ChatLog chatLog;
	private static JTextField messageField;
	private static JLabel turnLabel;
	private static TicTacToe tictactoe;
	private static JButton quitButton;
	public boolean turn;
	private String partner;
	String username;
	Service server;
	
	/**
	 * Create the application.
	 * @throws RemoteException 
	 */
	public ClientGUI(String username, Service server) throws RemoteException{
		this.server = server;
		this.username = username;
		turn = false;
		initialize();
		
		messageField.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	    	chatLog.updateChat(username + " : " + messageField.getText());
	    	try {
	    		server.sendMessage(username, messageField.getText());
	    		messageField.setText("");
	    	} catch (RemoteException e1) {
	    		System.out.println("no");
			}
	    }
		});
		
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(!tictactoe.isGameOver) {
						server.forfeitGame(username);
					}
					server.unregister(username);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		
		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	            try {
	            	if(!tictactoe.isGameOver) {
						server.forfeitGame(username);
					}
	            	server.unregister(username);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
	        }
	    });
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
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
		
		tictactoe = new TicTacToe(server, username);
		tictactoe.setBounds(10, 11, 280, 278);
		JPanel panel = new JPanel();
		panel.setBounds(138, 67, 300, 300);
		getContentPane().add(panel);
		panel.setLayout(null);
		panel.add(tictactoe);
		
		setVisible(true);
		
	}
	
	
	public void startTimer() {
		countdown = 20;
		timerPane.setFont(new Font("Tahoma", Font.PLAIN, 30));
		updateTimerDisplay();
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
        
        timer.start();
	}
	
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
	
	
	public void announceWinner(String winner) {
		turnLabel.setText("Player " + winner + " wins!");
		tictactoe.GameOver();
		updateTimerDisplay();
	}

	
	public void showMessage(String username, String message) {
		chatLog.updateChat(username + " : " + message);
	}
	
	public void turn(boolean nextTurn) {
		turn = nextTurn;
		tictactoe.setTurn(nextTurn);
		if(turn) {
			turnLabel.setText(username + "'s turn (" + tictactoe.currentPlayer + ")");
			startTimer();
		}
		else {
			char partnerSymb;
			countdown = -1;
			updateTimerDisplay();
			if(tictactoe.currentPlayer == 'O') {
				partnerSymb = 'X';
			}
			else {
				partnerSymb = 'O';
			}
			turnLabel.setText(partner + "'s turn (" + partnerSymb + ")");
		}
	}
	
	public void getPartner(String partner) {
		this.partner = partner;
	}
	
	public void updateBoard(char[][] board) {
		tictactoe.board = board;
		tictactoe.displayBoard(board);
	}

	public void getSymbol(char symb) {
		tictactoe.currentPlayer = symb;
	}

	public void announceDraw() {
		turnLabel.setText("Match drawn");
		tictactoe.GameOver();
		updateTimerDisplay();
	}
	
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
		newButton.setBounds(44, 43, 89, 23);
		frame.getContentPane().add(newButton);
		
		JButton qButton = new JButton("Quit");
		qButton.setBounds(143, 43, 89, 23);
		frame.getContentPane().add(qButton);
		
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				try {
					server.newGame(username);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		qButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					server.unregister(username);
				} catch (RemoteException e1) {
					System.out.println("removed");;
				}
				System.exit(0);
			}
		});
		
		
		frame.setVisible(true);
	}

	public void resetGUI() {
		tictactoe.resetBoard();
		chatLog.resetChat();
		timerPane.setText("Finding player");
		turnLabel.setText("");
	}
	
}
