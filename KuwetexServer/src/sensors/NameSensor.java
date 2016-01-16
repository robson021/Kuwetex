package sensors;

import other.Cat;

public class NameSensor implements AbstractSensor {
	
	/**
	 * @param cat - Reference to the cat that will be examined.
	 * @return Name of the examined cat.
	 * */
	@Override
	public String examine(Cat cat) {
		return cat.getName();
	}
	
}
