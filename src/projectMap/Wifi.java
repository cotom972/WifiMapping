package projectMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.vecmath.Point3d;

public class Wifi implements Comparable<Wifi>{
	
	static final int HOW_MANY_PARAMS = 11; 	// How many parameters expected from input.
	static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);
	
	// Parameters given from an external query to the constructor:
	private Mac mac;
	private String ssid;
	private String authMode;
	private String firstSeen;
	private double channel;
	private double rssi;
	private double accuracy;
	private String type;
	private Point3d Point3d;
	private Date date; 
	private String[] rawQuery;

	
	// ------------------------------------------ Constructors -----------------------------------------------:
	/**
	* Constructor of Wifi object.
	 * @param mac - Mac Address
	 * @param ssid - SSID of wifi.
	 * @param authMode - AuthMode.
	 * @param firstSeen - Time and Date when wifi was listed.
	 * @param channel - Channel of wifi.
	 * @param rssi - RSSI - Signal's strength of wifi.
	 * @param lat - Geo: latitude.
	 * @param lon - Geo: longitude
	 * @param alt - Geo: Altitude.
	 * @param accuracy - Accuracy of when the wifi was measured.
	 * @param type - Type of wifi (WIFI/3gp/etc).
	 * @throws Exception - Throws exception if one of the variables aren't in right format. (Specifically in parsing time and date to 'Date' object).
	 */
	public Wifi(String mac,String ssid,String authMode,String firstSeen, double channel,double rssi, double lat, double lon, double alt, double accuracy,String type) throws Exception {
		
		this.mac = new Mac(mac);
		this.ssid = ssid;
		this.authMode = authMode;
		this.firstSeen = firstSeen;
		this.channel = channel;
		this.rssi = rssi;
		this.accuracy = accuracy;
		this.type = type;
		this.Point3d = new Point3d(lon,lat,alt);
		
		try {
			this.date = SIMPLE_DATE_FORMAT.parse(firstSeen);
		}
		catch(ParseException e) {
			try
			{
				this.date = new SimpleDateFormat("yy-mm-dd hh:mm:ss").parse(firstSeen);
			}
			catch(ParseException e1) {
				throw new Exception("Could not parse string time to date object.");
			}

		}
	}

	/**
	 * Wifi constructor
	 * @param values - All variables of Wifi object given in a String[] array
	 * @throws Exception - If String[] doesn't hold all parameters / If an error accured when parsing timd and date to 'Date' object.
	 */
	public Wifi(String[] values) throws Exception {

		if (values.length!=HOW_MANY_PARAMS)  // check to see if enough values are given to the constructor
			throw new Exception("String[] should hold "+Integer.toString(HOW_MANY_PARAMS)+" vaules exactly.");
		else {
			this.mac = new Mac(values[0]);
			this.ssid = values[1];
			this.authMode = values[2];
			this.firstSeen = values[3];
			this.channel = Double.parseDouble(values[4]);
			this.rssi = Double.parseDouble(values[5]);
			this.accuracy = Double.parseDouble(values[9]);
			this.type = values[10];
			this.rawQuery = Arrays.copyOf(values, values.length);
			this.Point3d = new Point3d( Double.parseDouble(values[7]),Double.parseDouble(values[6]),Double.parseDouble(values[8]));
			try {
				this.date = SIMPLE_DATE_FORMAT.parse(firstSeen);
			}
			catch(ParseException e) {
				try
				{
					this.date = new SimpleDateFormat("yy-mm-dd hh:mm:ss").parse(firstSeen);
				}
				catch(ParseException e1) {
					throw new Exception("Could not parse string time to date object.");
				}

			}
		}
	}
	/**
	 * Copy constructor
	 * @param other - Wifi object to be copied from.
	 * @throws Exception - When given Wifi object is null / Error accured in parsin time and date to 'Date' object.
	 */
	public Wifi(Wifi other) throws Exception {
		
		if(other==null)
			throw new Exception("Wifi given to be copied is null");	
		else {
			this.mac = new Mac(other.getMac().getAddress());
			this.ssid = other.ssid;
			this.authMode = other.authMode;
			this.firstSeen = other.firstSeen;
			this.channel = other.channel;
			this.rssi = other.rssi;
			this.accuracy = other.accuracy;
			this.type = other.type;
			this.rawQuery = Arrays.copyOf(other.rawQuery, other.rawQuery.length);
			this.Point3d = new Point3d(other.getPoint3d().x,other.getPoint3d().y,other.getPoint3d().z);
			try {
				this.date = SIMPLE_DATE_FORMAT.parse(firstSeen);
			}
			catch(ParseException e) {
				try
				{
					this.date = new SimpleDateFormat("yy-mm-dd hh:mm:ss").parse(firstSeen);
				}
				catch(ParseException e1) {
					throw new Exception("Could not parse string time to date object.");
				}

			}
		}
	}
	
	// --------------------------------------------- Getters -------------------------------------------------:
	/**
	 * 
	 * @return Accuracy measurment of when wifi was listed.
	 */
	public double getAccuracy() {
		return this.accuracy;
	}

	/**
	 * 
	 * @return MAC address of wifi.
	 */
	public Mac getMac() {
		return this.mac;
	}
	
	/**
	 * 
	 * @return SSID of wifi.
	 */
	public String getSsid() {
		return this.ssid;
	}
	
	/**
	 * 
	 * @return Wifi properties in a String[] array to be used in inside functions(String[]).
	 */
	public String[] getParams() {
		return this.rawQuery;
	}
	
	/**
	 * Gets the wifi's RSSI (Signal strength).
	 * @return	Signal's strength (RSSI): Poor({@literal-}100){@literal<}RSSI{@literal<}Strong(0)
	 */
	public double getRssi() {
		return this.rssi;
	}

	/**
	 * 
	 * @return AuthMode of wifi.
	 */
	public String getAuthMode() {
		return this.authMode;
	}
	
	/**
	 * 
	 * @return Time and Date when wifi was listed.
	 */
	public String getFirstSeen() {
		return this.firstSeen;
	}
	
	/**
	 * 
	 * @return Channel of wifi.
	 */
	public double getChannel() {
		return this.channel;
	}
	
	/**
	 * 
	 * @return Geo: Latitude.
	 */
	public double getLat() {
		return this.Point3d.y;
	}

	/**
	 * 
	 * @return Geo: longitude.
	 */
	public double getLon() {
		return this.Point3d.x;
	}
	
	/**
	 * 
	 * @return Geo: Altitude.
	 */
	public double getAlt() {
		return this.Point3d.z;
	}
	
	/**
	 * 
	 * @return Type of connection (Wifi/3gp/etc.)
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * 
	 * @return Geo: Location of wifi in a 'Point' object. (to be used in inside functions).
	 */
	public Point3d getPoint3d() {
		return this.Point3d;
	}
	
	/**
	 * 
	 * @return ime and Date when wifi was listed in a 'Date' object.
	 */
	public Date getDate() {
		return this.date;
	}
	
	// --------------------------------------------------------------------------------------------------------
	/**
	 * This function compares the strength of the signal of a current Wifi object, to one given as an argument in the function.
	 * It returns -1 if current is weaker, 0 if they are equal, 1 if it stronger.
	 */
	@Override

	public int compareTo(Wifi other) {
		if(this.rssi>other.getRssi())
			return 1;
		else if(this.rssi==other.getRssi())
			return 0;
		else
			return -1;
	}

	/**
	 * This function compares 2 Wifi objects by their MAC address.
	 * @param o1 - First Wifi to compare with.
	 * @param o2 - Second Wifi to compare to.
	 * @return returns 1 if o1 is "bigger" or has the same mac address but stronger signal. 0 if both same, 
	 */
	public int compareByMac(Wifi o1, Wifi o2) {
		return o1.getMac().getAddress().compareToIgnoreCase(o2.getMac().getAddress());
	}

	public boolean equalTo(Wifi other) {
		return (
				this.mac.equalTo(other.getMac()) &&
				this.accuracy == other.accuracy &&
				this.authMode == other.authMode &&
				this.date.compareTo(other.date)==0 &&
				this.Point3d.x == other.Point3d.x &&
				this.Point3d.y == other.Point3d.y &&
				this.Point3d.z == other.Point3d.z &&
				this.accuracy == other.accuracy &&
				this.rssi == other.rssi &&
				this.ssid.contentEquals(other.ssid) &&
				this.type.contentEquals(other.type)	&&
				this.getChannel()==other.getChannel()
				);
	}
}
