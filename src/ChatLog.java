import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ChatLog extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textArea0;
	private JTextArea textArea1;
	private JTextArea textArea2;
	private JTextArea textArea3;
	private JTextArea textArea4;
	private JTextArea textArea5;
	private JTextArea textArea6;
	private JTextArea textArea7;
	private JTextArea textArea8;
	private JTextArea textArea9;
	
	public ChatLog() {
		setBackground(new Color(255, 255, 255));
		setBounds(10, 11, 185, 290);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gbl_panel);
		
		textArea9 = new JTextArea();
		textArea9.setEditable(false);
		GridBagConstraints gbc_textArea9 = new GridBagConstraints();
		gbc_textArea9.insets = new Insets(0, 0, 5, 0);
		gbc_textArea9.fill = GridBagConstraints.BOTH;
		gbc_textArea9.gridx = 0;
		gbc_textArea9.gridy = 0;
		add(textArea9, gbc_textArea9);
		
		textArea8 = new JTextArea();
		textArea8.setEditable(false);
		GridBagConstraints gbc_textArea8 = new GridBagConstraints();
		gbc_textArea8.insets = new Insets(0, 0, 5, 0);
		gbc_textArea8.fill = GridBagConstraints.BOTH;
		gbc_textArea8.gridx = 0;
		gbc_textArea8.gridy = 1;
		add(textArea8, gbc_textArea8);
		
		textArea7 = new JTextArea();
		textArea7.setEditable(false);
		GridBagConstraints gbc_textArea7 = new GridBagConstraints();
		gbc_textArea7.insets = new Insets(0, 0, 5, 0);
		gbc_textArea7.fill = GridBagConstraints.BOTH;
		gbc_textArea7.gridx = 0;
		gbc_textArea7.gridy = 2;
		add(textArea7, gbc_textArea7);
		
		textArea6 = new JTextArea();
		textArea6.setEditable(false);
		GridBagConstraints gbc_textArea6 = new GridBagConstraints();
		gbc_textArea6.insets = new Insets(0, 0, 5, 0);
		gbc_textArea6.fill = GridBagConstraints.BOTH;
		gbc_textArea6.gridx = 0;
		gbc_textArea6.gridy = 3;
		add(textArea6, gbc_textArea6);
		
		textArea5 = new JTextArea();
		textArea5.setEditable(false);
		GridBagConstraints gbc_textArea5 = new GridBagConstraints();
		gbc_textArea5.insets = new Insets(0, 0, 5, 0);
		gbc_textArea5.fill = GridBagConstraints.BOTH;
		gbc_textArea5.gridx = 0;
		gbc_textArea5.gridy = 4;
		add(textArea5, gbc_textArea5);
		
		textArea4 = new JTextArea();
		textArea4.setEditable(false);
		GridBagConstraints gbc_textArea4 = new GridBagConstraints();
		gbc_textArea4.insets = new Insets(0, 0, 5, 0);
		gbc_textArea4.fill = GridBagConstraints.BOTH;
		gbc_textArea4.gridx = 0;
		gbc_textArea4.gridy = 5;
		add(textArea4, gbc_textArea4);
		
		textArea3 = new JTextArea();
		textArea3.setEditable(false);
		GridBagConstraints gbc_textArea3 = new GridBagConstraints();
		gbc_textArea3.insets = new Insets(0, 0, 5, 0);
		gbc_textArea3.fill = GridBagConstraints.BOTH;
		gbc_textArea3.gridx = 0;
		gbc_textArea3.gridy = 6;
		add(textArea3, gbc_textArea3);
		
		textArea2 = new JTextArea();
		textArea2.setEditable(false);
		GridBagConstraints gbc_textArea2 = new GridBagConstraints();
		gbc_textArea2.insets = new Insets(0, 0, 5, 0);
		gbc_textArea2.fill = GridBagConstraints.BOTH;
		gbc_textArea2.gridx = 0;
		gbc_textArea2.gridy = 7;
		add(textArea2, gbc_textArea2);
		
		textArea1 = new JTextArea();
		textArea1.setEditable(false);
		GridBagConstraints gbc_textArea1 = new GridBagConstraints();
		gbc_textArea1.insets = new Insets(0, 0, 5, 0);
		gbc_textArea1.fill = GridBagConstraints.BOTH;
		gbc_textArea1.gridx = 0;
		gbc_textArea1.gridy = 8;
		add(textArea1, gbc_textArea1);
		
		textArea0 = new JTextArea();
		textArea0.setEditable(false);
		GridBagConstraints gbc_textArea0 = new GridBagConstraints();
		gbc_textArea0.fill = GridBagConstraints.BOTH;
		gbc_textArea0.gridx = 0;
		gbc_textArea0.gridy = 9;
		add(textArea0, gbc_textArea0);
		
	}
	
	public void updateChat(String message) {
		textArea9.setText(textArea8.getText());
		textArea8.setText(textArea7.getText());
		textArea7.setText(textArea6.getText());
		textArea6.setText(textArea5.getText());
		textArea5.setText(textArea4.getText());
		textArea4.setText(textArea3.getText());
		textArea3.setText(textArea2.getText());
		textArea2.setText(textArea1.getText());
		textArea1.setText(textArea0.getText());
		textArea0.setText(message);
	}

}
