package kuwetexserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import network.Message;

/**
 * This class represents client thread.
 * Client is connected to the server and it sends messages.
 * Task of the server is to response to the client in the run method.
 * */
class ClientWorker implements Runnable {
	private final Socket socket;	
	private final ObjectInputStream in;
	private final ObjectOutputStream out;
	private final int ID;	
	private final Map<Integer, Socket> clientMap;
	//private static volatile int online = 0;
		
	/**
	 * Constructor for new client that is connected to the server via socket.
	 * @param s - the socket
	 * @param map - reference to the map of clients. Every instance of this class is added to the map.
	 * @throws IOException
	 * */
	public ClientWorker(Socket s, int id, Map<Integer, Socket> map) throws IOException {
		socket = s;
		ID = id;
		clientMap = map;
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());
		//online++;
	}
	
	/**
	 * Runs the client thread. The loop inside is done when the socket is closed.
	 * */
	@Override
	public void run() {
		Message message;
		while (socket.isConnected()) 
		{			
				try {
					message = (Message) in.readObject();
					response(message);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
					System.out.println("Error, user #" + ID);
					logOutUser();
					break;
				}							
		}
	}
	/**
	 * Do some action and sends response message to the client.
	 * The action depends on header of the message given as an argument.
	 * @param message - message from the client.
	 * */
	private void response(Message message) {
		System.out.println("Message from client #" + ID + ": " + message.getMessage());
		switch (message.getHEADER()) {			
		case Message.LOG_ME_IN: {
			clientMap.put(ID, socket); // add me to the map
			System.out.println("Online now: "+clientMap.size());
			message = new Message("You are connected.", Message.LOG_ME_IN);
			sendMessage(message);
			break;
		}
		case Message.LOG_OUT: {
			logOutUser();
			break;
		}
		case Message.GET_RAPORT: {
			String raport = KuwetexServer.getRaport();
			message = new Message(raport, Message.GET_RAPORT);
			sendMessage(message);
			break;
		}
		case Message.FORCE_CLEANING: {
			String m = "Cleaning done.";
			try {
				KuwetexServer.clearLitterBox(true); // true = forced cleaning
			} catch (InterruptedException e) {
				e.printStackTrace();
				m = "Error. Failed to clean the litter box.";
			} finally {
				message = new Message(m, Message.FORCE_CLEANING);
				sendMessage(message);
			}
			break;
		}
		case Message.GET_RECOMMENDATIONS: {
			String recommendations = KuwetexServer.getDataBank().getPrescriptions();
			message = new Message(recommendations, Message.GET_RECOMMENDATIONS);
			sendMessage(message);
			break;
		}
		default: {
			System.out.println("Error, no such case."); 
			logOutUser(); // disconnect user
			break;
		}
		} // end of switch
	}
	
	/**
	 * When user disconnects this method closes socket (if it is still open).
	 * It also deletes the user for the client map.
	 * */
	private void logOutUser() {
		if (!socket.isClosed())
			try {
				System.out.println("Logging out user #" + ID);
				socket.close();
			} catch (IOException e1) {
			}
		clientMap.remove(ID);
		//online--;
		System.out.println("User #"+ID+" has disconnected. Online now: " + clientMap.size());
	}
	
	/**
	 * Sends response to the client.
	 * @param message - the Message class object that will be sent.
	 * */
	private void sendMessage (Message message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error while sending message back to the client #"+ID);
			// recursion try
			sendMessage(message);
		}
	}

}
