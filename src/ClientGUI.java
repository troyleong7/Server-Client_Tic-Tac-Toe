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
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class ClientGUI extends JFrame {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Timer timer;
	private static int countdown;
	private static JTextPane timerPane;
	private static JTextArea chatLog;
	private static JTextField messageField;
	private static JLabel turnLabel;
	private static TicTacToe tictactoe;
	public boolean turn = false;
	String username;
	Service server;
	
	/**
	 * Create the application.
	 * @throws RemoteException 
	 */
	public ClientGUI(String username, Service server) throws RemoteException{
		this.server = server;
		this.username = username;
		initialize();
		
		messageField.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	    	chatLog.append(username + " : " + messageField.getText() + "\n");
	    	try {
	    		server.sendMessage(username, messageField.getText());
	    		messageField.setText("");
	    	} catch (RemoteException e1) {
	    		System.out.println("no");
			}
	    }
		});
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		System.out.println(username);
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
		
		JButton quitButton = new JButton("QUIT");
		quitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		quitButton.setBounds(10, 285, 105, 23);
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		getContentPane().add(quitButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(448, 34, 196, 312);
		getContentPane().add(scrollPane);
		
		chatLog = new JTextArea();
		chatLog.setLineWrap(true);
		chatLog.setEditable(false);
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
		timerPane.setText("Waiting for player");
		
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
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdown--;
                updateTimerDisplay();
                if (countdown == 0) {
                    timer.stop();
                }
            }
        });
        
        updateTimerDisplay();
        timer.start();
	}
	
	private static void updateTimerDisplay() {
        int seconds = countdown % 60;

        String timeString = String.format("%02d", seconds);
        timerPane.setText("Timer: \n" + timeString);
    }
	
	public static void announceWinner(String announcement) {
		chatLog.append(announcement);
	}

	public void playerFound() {
		startTimer();
	}
	
	public void showMessage(String username, String message) {
		chatLog.append(username + " : " + message + "\n");
	}
	
	public void turn(boolean nextTurn) {
		turn = nextTurn;
		tictactoe.setTurn(nextTurn);
	}
	
	
	public void updateBoard(char[][] board) {
		tictactoe.board = board;
		tictactoe.displayBoard(board);
	}
}
