package kuwetexserver;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import network.Message;
import other.Cat;
import sensors.*;

/**
 * Main class of the server.
 * Runs many threads and loop that waits for connections for clients is located here.
 * */
public class KuwetexServer {
	private static volatile int idCounter = 0;
	private static volatile int litterBoxDirtiness = 0;
	private static final int MAX_DIRTINESS_LEVEL = 7;
	public static final Random random = new Random();
	public static final int ROLL = 5_000; // for random time generation
	//public static final String Separator = ";";
	
	private Map<Integer, Socket> clientMap;
	private boolean isWorking = false;
	private ServerSocket serverSocket;
	
	private static final DataBank dataBank = new DataBank();
	private static final Lock lock = new ReentrantLock();
	
	// sensors
	private static final AbstractSensor eyeSensor = new EyeSensor(),
								weightMachine = new WeightSensor(),
								nameRecognization = new NameSensor(),
								healtChecker = new HealthSensor();
	
	// constructor
	public KuwetexServer() throws IOException {
		clientMap = new HashMap<>();
		serverSocket = new ServerSocket(Message.PORT);
	}
	/**
	 * Starts the server.
	 * Allows clients to connect.
	 * Creates virtual cats.
	 * */
	public void startServer() {
		isWorking = true;		
		// clearness monitor thread
		new Thread(new CleaningSystemRunnable()).start();
		
		// creating virtual cats
		for (int i=0; i<Cat.NAMES.length; i++) {
			Thread t = new Thread(new Cat(i, i));
			t.start();
		}
		System.out.println("server started");		
		while (isWorking)
		{
			try {
				Socket socket = serverSocket.accept();		
				// new client thread
				Thread t = new Thread(new ClientWorker(socket, idCounter++, clientMap));
				t.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("server closed");
	}
	
	/**
	 * When cat thread wants to use litter box, the method is used.
	 * It is thread safety. Method has inner ReentrantLock.
	 * @throws InterruptedException
	 * */
	public static void useLitterBox(Cat cat) throws InterruptedException {
		String eyes = eyeSensor.examine(cat);
		String weight = weightMachine.examine(cat);
		String name = nameRecognization.examine(cat);
		String healthStatus = healtChecker.examine(cat);
		
		int time = random.nextInt(ROLL);
		long t0, t1;
		lock.lock(); // only one cat can use litter box
		try {
			t0 = System.currentTimeMillis();
			System.out.println("Cat "+name+" has entered.");
			Thread.sleep(time);
			t1 = System.currentTimeMillis();
			litterBoxDirtiness += random.nextInt(2) + 1; // 1=small poop, 2=big poop
		} finally {
			lock.unlock();
		}
		
		// save data
		System.out.println("Cat "+name+" has exited.");
		// total time spent in litter box (t1-t0)
		updateData((t1-t0), name, eyes, weight, healthStatus);
	}

	/**
	 * After the cat used the litter box, this method updates data in the Data Bank.
	 * @param name - name of the cat.
	 * @param eyes - eyes color of the cat.
	 * @param weight - current cat weight (kg).
	 * @param timeSpent - the time of last defecation.
	 * @param health - health status of the cat.
	 * */
	private static void updateData(long timeSpent, String name, String eyes, String weight, String health) {	
		System.out.println("Updating history");
		dataBank.addNewRecord(name, eyes, weight, timeSpent, health);
	}
	
	/**
	 * Gets report for the Data Bank as a String.
	 * @return String representation of all data that is stored.
	 * */
	public static String getRaport() {
		return dataBank.toString();
	}
	
	/**
	 * Thread that checks whether the litter box is dirty and needs cleaning.
	 * */
	private class CleaningSystemRunnable implements Runnable {
		@Override
		public void run() {
			while (true)
			{
				try {
					clearLitterBox(false); // not forced
					Thread.sleep(8_000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
		
	/**
	 * Clears the litter box. Waits if cat is inside.
	 * It can be triggered for the user or by the cleaning monitor thread.
	 * Method is thread safe.
	 * @param forcedClean - true = cleaning forced by user, false = triggered by cleaning thread.
	 * */
	public static void clearLitterBox(boolean forcedClean) throws InterruptedException {
		lock.lock(); // There can not be any cat inside during cleaning process
		try {
			if ( (litterBoxDirtiness >= MAX_DIRTINESS_LEVEL) || forcedClean) {
				if (forcedClean) {
					System.out.println("Cleaning is forced by user.");
				}				
				System.out.println("Cleaning process is running. Dirtiness level: "+litterBoxDirtiness);
				litterBoxDirtiness = 0;
				Thread.sleep(random.nextInt(ROLL)); // cleaning time
			} else {
				System.out.println("CLEANING SYSTEM: Dirtness at normal level. Not cleaning.");
			}
		} finally {
			lock.unlock();			
		}
	}
	
	/**
	 * @return Instance of the DataBank.
	 * */
	public static final DataBank getDataBank() {
		return dataBank;
	}
	
	/**
	 * @param args - no needed
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new KuwetexServer().startServer();
	}

}
