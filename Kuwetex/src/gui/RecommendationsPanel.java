package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import kuwetexclient.KuwetexClient;
import network.Connection;
import network.Message;

/**
 * Recommendations are displayed here.
 * If you click the button client sends message to the server and as a response,
 * it will return information about cats.
 * */
@SuppressWarnings("serial")
public class RecommendationsPanel extends JPanel {
	private final Connection connection;
	private JButton getRecommendationsButton, backButton;
	private JTextArea textArea;
	
	/**
	 * Constructor.
	 * @param conn - reference to the Connection class object.
	 * */
	public RecommendationsPanel (Connection conn) {
		super(new BorderLayout());
		connection = conn;
		JPanel southPanel = new JPanel(new FlowLayout()),
			   centerPanel = new JPanel();
		
		textArea = new JTextArea(MainPanel.DEFAULT_COLLUMN_SIZE, MainPanel.DEFAULT_COLLUMN_SIZE * 3);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		centerPanel.add(new JScrollPane(textArea));
		
		getRecommendationsButton = new JButton("Recommendations");
		backButton = new JButton("Go back to main panel");
		getRecommendationsButton.addActionListener(new GetRecommendationsButtonHandler());
		backButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				KuwetexClient.swapPanel(0);
			}
		});
		
		southPanel.add(getRecommendationsButton);
		southPanel.add(backButton);
		
		add (centerPanel, BorderLayout.CENTER);
		add (southPanel, BorderLayout.SOUTH);
	}
	
	private class GetRecommendationsButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {			
			String text = connection.sendNewMessage(null, Message.GET_RECOMMENDATIONS);
			textArea.append(text);
			textArea.append("\n");
			
			try {
				KuwetexClient.saveToFile(text, KuwetexClient.RECOMMENDATIONS_FILE);
				textArea.append("Saved to file: " + KuwetexClient.RECOMMENDATIONS_FILE);
				textArea.append("\n\n");
			} catch (IOException e) {
				e.printStackTrace();
				textArea.append("Error. Could not save to file.\n\n");
			}
		}		
	}
	
}
