package sensors;

import other.Cat;

public interface AbstractSensor {
	/**
	 * Examines the cat.
	 * @param cat - Reference to the cat that will be examined.
	 * @return The cat's attribute that was detected.
	 */
	public abstract String examine(Cat cat);
}
