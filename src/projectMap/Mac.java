package projectMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mac {

	private String mac;
	
	/** 
	 * Constructs a new Mac object
	 * @param mac
	 * @throws Exception 
	 */
	public Mac(String mac) throws Exception {
		if(isValidMac(mac)) {
			this.mac = mac;
		}
		else {
			this.mac = "N/A";
		}
	}
	
	/**
	 * Validates given MAC  address
	 * Validates given MAC  address
	 * @param mac Address to be validated
	 * @return True if address valid, false otherwise.
	 * @throws Exception 
	 */
	private boolean isValidMac(String mac) throws Exception { 
		try {
		Pattern p = Pattern.compile("^((([0-9A-Fa-f]{2}:){5})|(([0-9A-Fa-f]{2}-){5}))[0-9A-Fa-f]{2}$");
        Matcher m = p.matcher(mac);
        return m.matches();
		}
        catch(Exception e) {
        	throw new Exception(mac+" error");
        }
    }
	
	/**
	 * Returns the mac address.
	 * @return MAC Address
	 */
	public String getAddress() {
		return this.mac;
	}
	
	
	/**
	 * Returns mac's address in a String type.
	 */
	public String toString() {
		return this.mac;
	}
}

