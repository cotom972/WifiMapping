package projectMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mac{

	private String mac;
	
	// ----------------------------------- Constructors ---------------------------------------------:
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
			int alignCenter = (17-Main.NO_INPUT_STRING.length())/2;
			this.mac = "";
			for(int i=0; i<alignCenter;i++)
				this.mac+=" ";
			this.mac+=Main.NO_INPUT_STRING;
			for(int i=0; i<alignCenter;i++)
				this.mac+=" ";
		}
	}
	/**
	 * Copy constructor
	 * @param other Mac object to be copied.
	 * @throws Exception
	 */
	public Mac(Mac other) throws Exception{
		this.mac = other.mac;
	}
	
	// ------------------------------------ Functions: ----------------------------------------------:
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
	/**
	 * 
	 * @param Mac to be compared to
	 * @return True if both has the same address, False otherwise.
	 */
	public boolean equalTo(Mac other) {
		return (this.getAddress().toLowerCase().contentEquals(other.getAddress().toLowerCase()));
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mac == null) ? 0 : mac.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {

		Mac other = (Mac) obj;
		return (this.getAddress().equalsIgnoreCase(other.getAddress()));
		
	}


	
}

