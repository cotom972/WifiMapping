package projectMap;

public class SignalExtras{

	
	/**
	 * Calculates and returns signal's weight by: 1/signal^2.
	 * @param signal Signal to weigh
	 * @return Weight of signal.
	 */
	public static double getWeight(Signal signal) {
		return (1/(signal.getStrength()*signal.getStrength()));
	}
	
	
}
