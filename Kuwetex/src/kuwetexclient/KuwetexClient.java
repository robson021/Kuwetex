package kuwetexclient;

import java.awt.CardLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gui.MainPanel;
import gui.RecommendationsPanel;
import network.Connection;

/**
 * The main frame of the application. It holds other components.
 * Also, the class have defined static methods that are used by other classes.
 * */
public class KuwetexClient extends JFrame {
	private static final String TITLE = "KUWETEX 1.0";
	public static final String RAPORT_FILE = "res/data.txt";
	public static final String RECOMMENDATIONS_FILE = "res/recommendations.txt";
	
	private Connection connection = null;	
	private static JFrame applicationFrame;
	private JPanel mainPanel, recommendationsPanel = null;
	
	private static final Map<String, JPanel> panelMap = new HashMap<>(2);
	private static final String[] PANEL_NAMES = {"mainPanel", "recommPanel"};
	
	/**
	 * Constructor of the main class.
	 * @throws UnknownHostException, IOException
	 * */
	public KuwetexClient() throws UnknownHostException, IOException {
		super(TITLE);
		connection = new Connection();
		setLayout(new CardLayout());
		mainPanel = new MainPanel(connection);
		recommendationsPanel = new RecommendationsPanel(connection);
		
		add (mainPanel);	
		add (recommendationsPanel);
		
		panelMap.put(PANEL_NAMES[0], mainPanel);
		panelMap.put(PANEL_NAMES[1], recommendationsPanel);
	}
	/**
	 * Hides current visible panel and shows new one.
	 * @param panelID - id of the panel that will be visible now. 
	 */
	public static void swapPanel(int panelID) {
		for (JPanel p : panelMap.values())
			p.setVisible(false);
		
		panelMap.get(PANEL_NAMES[panelID]).setVisible(true);
	}
	
	/*public static KuwetexClient getInstance() {
		return (KuwetexClient) applicationFrame;
	} */
	
	
	/**
	 * Saves given text to the file. It is used to update data about cats.
	 * @param text - text that will be append to the file.
	 * @param PATH - path where the file will be created. Example: "res/recommendations.txt"
	 * @return true or false. If file was successfully created returns true. Otherwise method will return false.
	 * @throws IOException
	 * */
	@SuppressWarnings("finally")
	public static boolean saveToFile(final String text, final String PATH) throws IOException {
		FileWriter fw = null;
		boolean success = false;
		try {
			fw = new FileWriter(new File(PATH));
			String date = Calendar.getInstance().getTime().toString();
			date += "\n\n";
			fw.append(date);
			fw.append(text);
			success = true;
		} finally {
			if (fw != null)
				fw.close();
			return success;
		}
	}
	

	/**
	 * @param args - no needed
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				try {
					applicationFrame = new KuwetexClient();
				} catch (IOException e) {					
					e.printStackTrace();					
				}
				applicationFrame.setResizable(false);
				applicationFrame.pack();
				applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				applicationFrame.setLocationRelativeTo(null);
				applicationFrame.setVisible(true);
				System.out.println("LOADED");
			}
		});
		
	}

}
