package sensors;

import other.Cat;

/**
 * All the sensors that examines cat attributes should inherit this interface.
 * */
public interface AbstractSensor {
	/**
	 * Examines the cat.
	 * @param cat - Reference to the cat that will be examined.
	 * @return The cat's attribute that was detected.
	 */
	public abstract String examine(Cat cat);
}
