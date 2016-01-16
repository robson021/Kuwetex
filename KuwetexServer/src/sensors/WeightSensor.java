package sensors;

import kuwetexserver.KuwetexServer;
import other.Cat;

public class WeightSensor implements AbstractSensor {
	
	/**
	 * Method checks how heavy is the cat.
	 * @param cat - Reference to the cat that will be examined.
	 * @return Current weight of the cat.
	 * */
	@Override
	public String examine(Cat cat) {
		Integer weight = (KuwetexServer.random.nextInt(5)+2);
		return weight.toString() + " kg";
	}

}
