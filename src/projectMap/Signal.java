package projectMap;

public class Signal {
	
	private double  _strength;
	private final static double HIGH_SIGNAL_BOUND = 0;
	private static final double LOW_SIGNAL_BOUND = -120;
	private static final double NO_SIGNAL_VALUE= Double.MIN_VALUE;
	
	// ------------------------------------------ Constructors -----------------------------------------------:
	/**
	 * Constructor of Signal object
	 * @param signal signal strength.
	 */
	public Signal(double signal) {
		if(isValidSignal(signal)) {
			this._strength = signal;
		}
		else {
			this._strength = NO_SIGNAL_VALUE;
		}
	}
	/**
	 * Copy Constructor
	 * @param other Signal object to be copied.
	 */
	public Signal(Signal other) {
		this._strength = other._strength;
	}
	
	// -------------------------------------------- Functions ------------------------------------------------:
	/**
	 * Check if signal is valid according to range configured in finals: LOW_SIGNAL_BOUND, HIGH_SIGNAL_BOUND.
	 * @param signal The signal to be checked.
	 * @return True if is in range, False otherwise.
	 */
	public static boolean isValidSignal(double signal) {
		return (signal>=LOW_SIGNAL_BOUND && signal <=HIGH_SIGNAL_BOUND);
	}
	/**
	 * 
	 * @return Signal strength.
	 */
	public double getStrength() {
		return this._strength;
	}

	// ------------------------------------- WifiMapping Functions : ------------------------------------------:
	/**
	 * Calculates and returns signal's weight by: 1/signal^2.
	 * @param signal Signal to weigh
	 * @return Weight of signal.
	 */
	public double getWeight() {
		return Geo.getWeight(this);
	}
}
