package sensors;

import other.Cat;

public class EyeSensor implements AbstractSensor {
	
	/**
	 * @return Eye color of the examined cat.
	 * */
	@Override
	public String examine(Cat cat) {
		return cat.getEyes();
	}

}
