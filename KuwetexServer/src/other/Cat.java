package other;

import kuwetexserver.KuwetexServer;

/**
 * Cat imitation class.
 * It should be run as a thread.
 * Run method has an infinite loop that tries to use litter box for time to time.
 * */
public class Cat implements Runnable {
	public static final String[] NAMES = {"Dianusz", "Filemon", "Garfield"};
	public static final String[] EYES = {"blue", "red", "green"};
	
	// gender types
	public static final int MALE = 0;
	public static final int FEMALE = 1;
	
	private final String NAME;
	private final String EYE_COLOR;
	private final int GENDER;
	
	/**
	 * Constructor.
	 * @param name - name of the cat.
	 * @param eyes - eyes color of the cat.
	 * */
	public Cat(int name, int eyes) {
		NAME = NAMES[name];
		EYE_COLOR = EYES[eyes];
		GENDER = KuwetexServer.random.nextInt(2);
	}
	
	@Override
	public void run() {		
		while (true)
		{
			try {
				KuwetexServer.useLitterBox(this);
				Thread.sleep(KuwetexServer.random.nextInt(10_000 + KuwetexServer.ROLL));
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("Error while using litter box, cat - "+NAME);
			}
		}
	}
	
	/**
	 * @return Name of the cat.
	 * */
	public String getName() {
		return NAME;
	}
	
	/**
	 * @return Eyes color of the cat.
	 * */
	public String getEyes() {
		return EYE_COLOR;
	}
	
	/**
	 * @return Gender of the cat.
	 * */
	public int getGender() {
		return this.GENDER;
	}

}
