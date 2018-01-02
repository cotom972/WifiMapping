package projectMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.vecmath.Point3d;

public class Wifi implements Comparable<Wifi>{
	
	static final int HOW_MANY_PARAMS = 11; 	// How many parameters expected from input.
	static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	static final String DATE_FORMAT_WITH_SECONDS = "dd/MM/yyyy HH:mm:ss";
	static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);
	static final SimpleDateFormat SIMPLE_DATE_FORMAT_WITH_SECONDS = new SimpleDateFormat(DATE_FORMAT_WITH_SECONDS);

	// Parameters given from an external query to the constructor:
	private Mac mac;
	private String ssid;
	private String authMode;
	private String firstSeen;
	private double channel;
	private Signal Signal;
	private double accuracy;
	private String type;
	private Point3d Point3d;
	private Date date; 
	private String[] rawQuery;
	private WiggleScanner scanDetails;
	
	// ------------------------------------------ Constructors -----------------------------------------------:
	/**
	* Constructor of Wifi object. In case on of arguments is null/empty it assigns FINAL NO_INPUT to value.
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
	public Wifi(String mac,String ssid,String authMode,String firstSeen, Double channel,Double rssi, Double lat, Double lon, Double alt, Double accuracy,String type,WiggleScanner scan) throws Exception {
		
 		this.scanDetails = new WiggleScanner(scan);
		this.mac = new Mac(mac);
		this.ssid = checkInput(ssid);
		this.authMode = checkInput(authMode);
		this.firstSeen = checkInput(firstSeen);
		this.channel = channel == null ? Main.NO_INPUT_INT:channel;
		this.Signal = new Signal(rssi);
		this.accuracy = accuracy == null ? Main.NO_INPUT_INT:accuracy;
		this.type = checkInput(type);
		this.Point3d = new Point3d(lon==null? 0:lon,lat==null? 0: lat,alt==null?0:alt);
		
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
	 * Wifi constructor.  In case on of arguments is null/empty it assigns FINAL NO_INPUT to value.
	 * @param values - All variables of Wifi object given in a String[] array
	 * @throws Exception - If String[] doesn't hold all parameters / If an error accured when parsing timd and date to 'Date' object.
	 */
	public Wifi(String[] values, WiggleScanner scannedFrom) throws Exception {

		if (values.length!=HOW_MANY_PARAMS)  // check to see if enough values are given to the constructor
			throw new Exception("String[] should hold "+Integer.toString(HOW_MANY_PARAMS)+" vaules exactly.");
		else {
			this.scanDetails = new WiggleScanner(scannedFrom);
			this.mac = new Mac(values[0]);
			this.ssid = checkInput(values[1]);
			this.authMode = checkInput(values[2]);
			this.firstSeen = checkInput(values[3]);
			this.channel = values[4].contentEquals("") ? Main.NO_INPUT_INT: Double.parseDouble(values[4]);
			this.Signal = new Signal(Double.parseDouble(values[5]));
			this.accuracy = values[9].contentEquals("") ? Main.NO_INPUT_INT: Double.parseDouble(values[9]);
			this.type = checkInput(values[10]);
			this.rawQuery = Arrays.copyOf(values, values.length);
			try {
				this.Point3d = new Point3d( Double.parseDouble(values[7]),Double.parseDouble(values[6]),Double.parseDouble(values[8]));
			}
			catch(Exception e) {
				this.Point3d = new Point3d(Main.NO_INPUT_INT,Main.NO_INPUT_INT,Main.NO_INPUT_INT);
			}
			
			try {
				this.date = SIMPLE_DATE_FORMAT.parse(firstSeen);
			}
			catch(ParseException e) {
				try
				{
					this.date = new SimpleDateFormat("yy-mm-dd hh:mm:ss").parse(firstSeen);
				}
				catch(ParseException e1) {
					this.date = SIMPLE_DATE_FORMAT.parse("00/00/00 00:00");
				}
			}
		}
	}
	public Wifi(String[] values) throws Exception{
		this(values, null);
	}
	/**
	 * Copy constructor.  In case on of arguments is null/empty it assigns FINAL NO_INPUT to value.
	 * @param other - Wifi object to be copied from.
	 * @throws Exception - When given Wifi object is null / Error accured in parsin time and date to 'Date' object.
	 */
	public Wifi(Wifi other) throws Exception {
		
		if(other==null)
			throw new Exception("Wifi given to be copied is null");	
		else {
			this.scanDetails = new WiggleScanner(other.scanDetails);
			this.mac = new Mac(other.getMac().getAddress());
			this.ssid = other.ssid;
			this.authMode = other.authMode;
			this.firstSeen = other.firstSeen;
			this.channel = other.channel;
			this.Signal = new Signal(other.getSignal());
			this.accuracy = other.accuracy;
			this.type = other.type;
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
	public Signal getSignal() {
		return this.Signal;
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
	 * Sets new Latitude to Point3d object.
	 * @param lat
	 */
	public void setLat(double lat) {
		this.Point3d.y = lat;
	}
	/**
	 * 
	 * @return Geo: longitude.
	 */
	public double getLon() {
		return this.Point3d.x;
	}
	/**
	 * Sets new Longitude to Point3d object.
	 * @param lat
	 */
	public void setLon(double lon) {
		this.Point3d.x = lon;
	}
	/**
	 * 
	 * @return Geo: Altitude.
	 */
	public double getAlt() {
		return this.Point3d.z;
	}
	/**
	 * Sets new Altitude to Point3d object.
	 * @param lat
	 */
	public void setAlt(double alt) {
		this.Point3d.z = alt;
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
	 * Sets new Point3d location of wifi.
	 * @param point
	 */
	public void setPoint3d(Point3d point) {
		this.Point3d = new Point3d(point);
	}
	/**
	 * 
	 * @return ime and Date when wifi was listed in a 'Date' object.
	 */
	public Date getDate() {
		return this.date;
	}
	/**
	 * Refferenced to the getWeight from Signal.class.
	 * @return Returns value of getWeight() in 'signal.class'.
	 */
	public double getSignalWeight() {
		return this.Signal.getWeight();
	}
	/**
	 * Check if Wifi has all location coordinates.
	 * @return Returns true if Wifi has all Lat,Lon and Alt coordinates. False otherwise.
	 */
	public boolean hasLocation() {
		return (this.getPoint3d().x!=Main.NO_INPUT_INT && this.getPoint3d().y!=Main.NO_INPUT_INT && this.getPoint3d().z!=Main.NO_INPUT_INT);
	}
	public WiggleScanner getScanDetails() {
		return this.scanDetails;
	}
	// --------------------------------------------------------------------------------------------------------
	/**
	 * This function compares the strength of the signal of a current Wifi object, to one given as an argument in the function.
	 * It returns -1 if current is weaker, 0 if they are equal, 1 if it stronger.
	 */
	@Override
	public int compareTo(Wifi other) {
		if(this.Signal.getStrength()>other.Signal.getStrength())
			return 1;
		else if(this.Signal.getStrength()==other.Signal.getStrength())
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
	/**
	 * Compares 2 Wifi objects by their paramter values.
	 * @param other Wifi obj to compare to
	 * @return True if all Wifi object variables are the same, False otherwise.
	 */
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
				this.Signal.getStrength() == other.Signal.getStrength() &&
				this.ssid.contentEquals(other.ssid) &&
				this.type.contentEquals(other.type)	&&
				this.getChannel()==other.getChannel()
				);
	}
	/**
	 * Check if 2 Wifis has same mac address.
	 * @param other Wifi to compare mac with.
	 * @return True if both have the same MAC address. False otherwise.
	 */
	public boolean sameMacOf(Wifi other) {
		return (this.getMac().equalTo(other.getMac()));
	}
	public boolean hasMac(Mac mac) {
		return (this.getMac().equalTo(mac));
	}
	public String printDate() {
		String result = SIMPLE_DATE_FORMAT_WITH_SECONDS.format(this.date);
		return result;
	}
	/**
	 * Checks if given input is empty, if so return FINAL string for no input. otherwise, return input.
	 * @param input 
	 * @return final String NO_INPUT if no input was given. 'input' value if input wasn't empty.
	 */
	public static String checkInput(String input) {
		if(input == null || input.contentEquals("") || input.contentEquals(Main.NO_INPUT_STRING)) {
			return Main.NO_INPUT_STRING;
		}
		else {
			return input;
		}
	}

}
