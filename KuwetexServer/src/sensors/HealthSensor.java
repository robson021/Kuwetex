package sensors;

import java.util.Calendar;
import java.util.Random;

import kuwetexserver.KuwetexServer;
import other.Cat;

public class HealthSensor implements AbstractSensor{
	private static final String[] POOP_QUALITY = {"normal I", "normal II", "over average", "illness"},
								  FUR_QUALITY = {"soft", "dirty - need cleaning", "normal"};
	
	private static final int PREGNANT = 0, PREGNANCY_CHANCE = 20;	
	
	private static final Random r = KuwetexServer.random;
	
	/**
	 * Detects health condition of the cat.
	 * @param cat - Reference to the cat that will be examined.
	 * @return String representation of cat's health status.
	 * */
	@Override
	public String examine(Cat cat) {
		int p = r.nextInt(POOP_QUALITY.length);
		String poopQuality = POOP_QUALITY[p];
		String furQuality = FUR_QUALITY[r.nextInt(FUR_QUALITY.length)];		
		String healtStatus = "Fur quality: "+furQuality+", excrement: "+poopQuality;
		
		// pregnancy test
		if ((cat.getGender()==Cat.FEMALE) && r.nextInt(PREGNANCY_CHANCE) == PREGNANT) {
			healtStatus += "; Cat is PREGNANT!";
			System.out.println("Urine examination proved that cat is pregnant.");
		}
		
		if (p == (POOP_QUALITY.length-1)) { // the cat is ill
			KuwetexServer.getDataBank().addNewIllCat(cat.getName(), Calendar.getInstance().getTime().toString());
			System.out.println("Cat is ill. Added new prescriptions to Data Bank.");
		}
		
		return healtStatus;
	}
	
}
