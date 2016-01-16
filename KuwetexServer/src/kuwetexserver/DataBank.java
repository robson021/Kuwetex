package kuwetexserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The class holds all the information about cats attributes, illnesses
 * and litter box uses history.
 * */
public class DataBank {
	private static final List<Data> dataList = new ArrayList<>();
	private static final List<IllnessHistory> illnessHistory = new ArrayList<>();
	
	// for ill cats
	private static final String[] PRESCRIPTIONS = {"Go to doctor.", "Dig the grave.",
			"Do not do anything, cat will recover soon.", "Cat needs to stress out. Give him/her Prozac."}; 
	
	private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	private class Data {
		private final String catName;
		private final String eyeColor;
		private final long timeSpent;
		private final String dateOfexcrection;
		private final String weight;
		private final String health;
		
		/**
		 * Constructor.
		 * @param name - name of the cat.
		 * @param eyes - eyes color of the cat.
		 * @param weight - current cat weight (kg).
		 * @param time - the time of last defecation.
		 * @param health - health status of the cat.
		 * */
		Data(String name, String eyes, String weight, long time, String health) {
			catName = name; eyeColor = eyes; timeSpent = time; this.weight = weight; this.health = health;
			dateOfexcrection = Calendar.getInstance().getTime().toString();
		}
		/**
		 * Overridden method.
		 * @return String representation of the informations about the cat, that are stored in the object.
		 * */
		@Override
		public String toString() {
			return "* "+catName +"; "+ eyeColor+" eyes; "+"weight: "+weight+
					";  "+timeSpent +"ms, on day: "+dateOfexcrection + ";\n" + health;			
		}
	}
	
	private class IllnessHistory {
		String catName;
		String date;
		String recommendation;
		
		/**
		 * If cat is ill, recommendation for him/her is generated here.
		 * @param cat - cat name
		 * @param date - date of the analysis
		 * */
		public IllnessHistory(String cat, String date) {
			catName = cat; this.date = date;
			recommendation = PRESCRIPTIONS[KuwetexServer.random.nextInt(PRESCRIPTIONS.length)];
		}
		
		/**
		 * Overridden method.
		 * @return String representation of the cat, his/her illness and recommendations for owner.
		 * */
		@Override
		public String toString() {			
			return catName + " was ill on " + date +". Recommendation:\n" + recommendation;
		}
	}
	
	/**
	 * Adds new record to the list of cats' data.
	 * @param name - name of the cat.
	 * @param eyes - eyes color of the cat.
	 * @param weight - current cat weight (kg).
	 * @param time - the time of last defecation.
	 * @param health - health status of the cat.
	 * */
	public void addNewRecord (String name, String eyes, String weight ,long time, String health) {
		rwLock.writeLock().lock();
		try {
			dataList.add (new Data(name, eyes, weight, time, health));
		} finally {
			rwLock.writeLock().unlock();
		}
	}
	
	/**
	 * Adds new illness history to the list of illnesses.
	 * @param catName - name of the cat
	 * @param date - date of the analysis
	 * */
	public void addNewIllCat(String catName, String date) {
		rwLock.writeLock().lock();
		try {
			illnessHistory.add(new IllnessHistory(catName, date));
		} finally {
			rwLock.writeLock().unlock();
		}
	}
	/**
	 * Overridden method.
	 * @return String representation of all data about the cats stored in the Data Bank. 
	 * */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		rwLock.readLock().lock();
		try {
			for (Data d : dataList) {
				builder.append(d.toString());
				builder.append("\n");
			}
		} finally {
			rwLock.readLock().unlock();
		}
		return builder.toString();
	}
	
	/**
	 * Gathers the data of all cats' illnesses and gives prescriptions to the owner.
	 * @return String representation of the values.
	 * */
	public String getPrescriptions() {
		StringBuilder builder = new StringBuilder();
		rwLock.readLock().lock();
		try {
			for (IllnessHistory illness : illnessHistory) {
				builder.append("* ");
				builder.append(illness.toString());
				builder.append("\n");
			}
		} finally {
			rwLock.readLock().unlock();
		}
		return builder.toString();
	}
}

