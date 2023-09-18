import java.awt.EventQueue;
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

public class ClientGUI {

	private JFrame frame;
	private Timer timer;
	private int countdown;
	private JTextPane timerPane;
	private static JTextArea chatLog;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 670, 428);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTextPane gameLabel = new JTextPane();
		gameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		gameLabel.setToolTipText("");
		gameLabel.setBackground(SystemColor.menu);
		gameLabel.setEditable(false);
		gameLabel.setText("Distributed Tic-Tac-Toe");
		gameLabel.setBounds(10, 189, 105, 59);
		frame.getContentPane().add(gameLabel);
		
		JButton quitButton = new JButton("QUIT");
		quitButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		quitButton.setBounds(10, 285, 105, 23);
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		frame.getContentPane().add(quitButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(448, 34, 196, 312);
		frame.getContentPane().add(scrollPane);
		
		chatLog = new JTextArea();
		chatLog.setLineWrap(true);
		chatLog.setEditable(false);
		scrollPane.setViewportView(chatLog);
		
		JTextField messageField = new JTextField();
		messageField.setBounds(448, 344, 196, 34);
		frame.getContentPane().add(messageField);
		messageField.setColumns(10);
		messageField.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		       chatLog.append(messageField.getText() + "\n");
		       messageField.setText("");
		    }
		});
		
		JLabel chatLabel = new JLabel("Player Chat");
		chatLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		chatLabel.setHorizontalAlignment(SwingConstants.CENTER);
		chatLabel.setBounds(448, 11, 196, 23);
		frame.getContentPane().add(chatLabel);
		
		TicTacToe tictactoe = new TicTacToe();
		tictactoe.setBounds(10, 11, 280, 278);
		JPanel panel = new JPanel();
		panel.setBounds(138, 67, 300, 300);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		panel.add(tictactoe);
		
		JLabel turnLabel = new JLabel("Player turn");
		turnLabel.setOpaque(true);
		turnLabel.setBackground(SystemColor.text);
		turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
		turnLabel.setBounds(137, 33, 301, 34);
		frame.getContentPane().add(turnLabel);
		
		timerPane = new JTextPane();
		timerPane.setEditable(false);
		timerPane.setBackground(SystemColor.menu);
		timerPane.setFont(new Font("Tahoma", Font.PLAIN, 30));
		timerPane.setBounds(10, 34, 105, 105);
		
		StyledDocument doc = timerPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		frame.getContentPane().add(timerPane);
		
		countdown = 20;
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
	
	private void updateTimerDisplay() {
        int seconds = countdown % 60;

        String timeString = String.format("%02d", seconds);
        timerPane.setText("Timer: \n" + timeString);
    }
	
	public static void announceWinner(String announcement) {
		chatLog.append(announcement);
	}
}
