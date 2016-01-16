package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class has essential methods that provides connection with the server (Litter Box)
 * and it is responsible for sending and receiving messages. 
 * */
public class Connection {
	private Message message;
	private boolean isConnected = false;
	
	private Socket socket = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	
	public Connection() {		
	}
	
	/**
	 * If panel is disconnected this method attempts to connect to the LitterBox (server).
	 * @param msg - text that you want to send.
	 * @return - message from the server (response message).
	 * @throws - UnknownHostException, IOException
	 * */
	public String connectToServer(String msg) throws UnknownHostException, IOException {
		String msgBack = null;
		if (isConnected) return null;
		
		socket = new Socket(Message.ADRESS, Message.PORT);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		
		msgBack = sendNewMessage(msg, Message.LOG_ME_IN);
		isConnected = true;
		
		return msgBack;
	}
	
	/**
	 * Sends new message to the server (Litter Box), then waits for response and returns
	 * message for the server.
	 * @param text - text that you want to send to the server.
	 * @param header - header that is used to recognize action on server's side.
	 * Use one of static final headers for Message class.
	 * @return Response (text message) for the server.
	 * */
	public String sendNewMessage (String text, int header) {
		message = new Message(text, header);		
		try {
			out.writeObject(message);
			out.flush();
			
			Message response = (Message) in.readObject();
			
			if (response.getHEADER() == message.getHEADER()) {
				return response.getMessage();
			} else {
				return "Error. Headers do not match.";
			}
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Error. Exeption";
		}
	}
	
	/**
	 * Method is used for disconnecting for the server (Litter Box).
	 * @return True if disconnected without any problems. False if exception occoured.
	 * */
	@SuppressWarnings("finally")
	public boolean closeConnection() {
		boolean tof = false;
		message = new Message("client has logged out", Message.LOG_OUT);		
		try {
			out.writeObject(message);
			out.flush();
			
			if (socket != null && !socket.isClosed())
				socket.close();			
			tof = true;			
			isConnected = false;
		} catch (IOException e) {			
			e.printStackTrace();
		} finally {
			return tof;
		}
	}
	
	/**
	 * Checks if socket is still connected to the server (Litter Box).
	 * @return True if socket is NOT closed. False if the socket is closed.
	 * */
	public boolean isConnected() {
		if (socket == null || socket.isClosed())
			isConnected = false;
		return isConnected;
	}
}
