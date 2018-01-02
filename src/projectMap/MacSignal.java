package projectMap;

public class MacSignal {
	private Mac _mac;
	private Signal _signal;

	/**
	 * Constructor
	 * @param mac	MAC address.
	 * @param signal Signal's strength.
	 * @throws Exception
	 */
	public MacSignal(Mac mac,Signal signal) throws Exception {
		this._mac = new Mac(mac);
		this._signal = new Signal(signal);
	}
}
