package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import kuwetexclient.KuwetexClient;
import network.Connection;
import network.Message;

/**
 * The main panel that provides interaction with user.
 * */
@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	public static final int DEFAULT_COLLUMN_SIZE = 20;
	
	private final Connection connection;
	
	private JTextArea textArea;
	private JButton connectButton, getReportButton, disconnectButton, forceCleanButton, recommButton;
	//private final JPanel thisPanel = this, recommendPanel;
	
	/**
	 * Constructor.
	 * @param conn - reference to the Connection class object.
	 * */
	public MainPanel(Connection conn) {
		super (new BorderLayout());		
		connection = conn;
		
		JPanel centerPanel = new JPanel();
		textArea = new JTextArea(DEFAULT_COLLUMN_SIZE, DEFAULT_COLLUMN_SIZE * 3);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		centerPanel.add(new JScrollPane(textArea));		
		add (centerPanel, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new FlowLayout());
		connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String msg ="New connection: " + Calendar.getInstance().getTime().toString();
				try {
					msg = connection.connectToServer(msg);
					textArea.append(msg + "\n");
					connectButton.setEnabled(false);
				} catch (IOException e) {					
					e.printStackTrace();
				}				
			}
		});
		southPanel.add (connectButton);
		
		getReportButton = new JButton("Raport");
		getReportButton.addActionListener(new ReportButtonHandler());
		southPanel.add(getReportButton);
		
		disconnectButton = new JButton("Diconnect");
		disconnectButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( ! connection.isConnected()) return;
				if (connection.closeConnection()) {
					checkConnection();
				} else {
					textArea.append("Error. Could not disconnect form the server.\n");
				}
			}
		});
		
		forceCleanButton = new JButton("Force Clean");
		forceCleanButton.addActionListener(new ForceCleanButtonHandler());
		
		recommButton = new JButton("Go to recommendations panel");
		recommButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( ! checkConnection()) return;				
				KuwetexClient.swapPanel(1); // show recommendations panel
			}
		});
		
		southPanel.add(forceCleanButton);
		southPanel.add(recommButton);		
		southPanel.add(disconnectButton);		
		
		add (southPanel, BorderLayout.SOUTH);
	}
	
	private class ReportButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {	
			if (! checkConnection()) return;
			String raport = connection.sendNewMessage(null, Message.GET_RAPORT);
			textArea.append(raport);
			//textArea.append("\n");
			try {
				if (KuwetexClient.saveToFile(raport, KuwetexClient.RAPORT_FILE)) {
					textArea.append("Saved to file: " + KuwetexClient.RAPORT_FILE);
					textArea.append("\n\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
				textArea.append("Error. Could not save to file.\n\n");
			}
		}
		
	}
	
	private class ForceCleanButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if ( ! checkConnection()) return;
			String raport = connection.sendNewMessage(null, Message.FORCE_CLEANING);
			textArea.append(raport);
			textArea.append("\n");
		}
		
	}
	
	private boolean checkConnection() {
		if ( ! connection.isConnected()) {
			// socket has been closed
			connectButton.setEnabled(true);
			textArea.append("You are not connected!\n");
			return false;
		}
		return true;
	}
}
