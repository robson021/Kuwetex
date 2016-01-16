package network;

import java.io.Serializable;

/**
 * Object of this class is used as a information sent between client and server.
 * */
@SuppressWarnings("serial")
public class Message implements Serializable {
	
	/* 
	 * For socket connection.
	 * Message is mutual to both client and server, so PORT and ADRESS are defined here.
	 */
	public static final int PORT = 4444;
	public static final String ADRESS = "localhost";
	
	// headers
	public static final int LOG_OUT = 0;
	public static final int LOG_ME_IN = 1;
	public static final int GET_RAPORT = 2;
	public static final int FORCE_CLEANING = 3;
	public static final int GET_RECOMMENDATIONS = 4;
	
	private final int HEADER;
	private final String MESSAGE_TEXT;
	
	public Message (String msg, int header) {
		MESSAGE_TEXT = msg; HEADER = header;
	}	

	/**
	 * @return the header
	 */
	public int getHEADER() {
		return HEADER;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return MESSAGE_TEXT;
	}

}
