package projectMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.vecmath.Point3d;

/**
 * Each DBWifiTimeStamp object represents 1 second in time, and contains 10 wifis that were scanned with the strongest signal at that specific time.
 * @author Tom Cohen
 */
public class DBWifiTimeStamp implements Comparable<DBWifiTimeStamp>{
	
	static final String DBTIMESTAMP_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	static final SimpleDateFormat SIMPLE_DATE_FORMATER = new SimpleDateFormat(DBTIMESTAMP_TIME_FORMAT);	
	static final int NUM_OF_WIFIS_PER_TIMESTAMP = 10;
	static final String BEGINNING_OF_DB_FILENAME = "DBWifiTimeStamp"; // all DB files must begin with this (DBWifiTimeStamp[....].csv)
	static final String NO_INPUT = "      N/A      ";
	static final double NO_INPUT_INT = Double.MIN_VALUE;
	public static final int NUM_OF_WIFIS_ON_DB_ROW = 10;

	private Date _timeStamp;
	private WifiList _wifis;
	private Point3d _location;
	private ArrayList<Mac> _macList = new ArrayList<Mac>();
	private WiggleScanner _wiggleScanner;
	private String _wifiNetworks; 
	
	// --------------------------------------- Constructors --------------------------------------------------:


	public DBWifiTimeStamp(Date date,WifiList wifis) throws Exception {
		
		WifiList resultWifiList = new WifiList(wifis);
		
		this._timeStamp = new Date(date.getTime()); 
		this._wifis = new WifiList();
		resultWifiList.filterDate(date, date); // Retrieve all wifis from list that were scanned in 'date'.
		resultWifiList.sortBySignal();		   // "Collect" only the ones with the strongest signal.
		
		int indexOfLast = NUM_OF_WIFIS_PER_TIMESTAMP<resultWifiList.getArrayList().size() ? NUM_OF_WIFIS_PER_TIMESTAMP:resultWifiList.getArrayList().size();
		for(int i=0; i<indexOfLast; i++) {
			this._wifis.getArrayList().add(resultWifiList.getArrayList().get(i));
		}
		this._location = Geo.estimateMacLocation(this._wifis, ""); // Estimate TimeStamp's location out of all locations of wifis that appear in the timestamp.
		this._macList = new ArrayList<Mac>();						
		Mac previous = this._wifis.getArrayList().get(0).getMac(); // Retrieve all mac appearence of current TimeStamp.
		for(Wifi wifi: this._wifis.getArrayList().subList(1, this._wifis.getArrayList().size())) {
			if(!wifi.getMac().equalTo(previous)) {
				this._macList.add(wifi.getMac());
				previous = wifi.getMac();
			}
		}
	}
	public DBWifiTimeStamp(DBWifiTimeStamp other) throws Exception {
	
		this._timeStamp = new Date(other.getTimeStampDate().getTime());	
		this._wifis = new WifiList(other.getWifis());
		this._location = new Point3d(other._location);
		this._wiggleScanner = new WiggleScanner(other.getWiggleScanner());
		this._wifiNetworks = other._wifiNetworks;
		if(this.getMacList()!=null)
			for(Mac mac: other.getMacList()) 
				this._macList.add(mac);
	}
	public DBWifiTimeStamp(String[] rawData) throws NumberFormatException, Exception {

 		this._wifis = new WifiList();
		// -------------------------- Build  Date -------------------------- //
		try {
		this._timeStamp = SIMPLE_DATE_FORMATER.parse(SIMPLE_DATE_FORMATER.format(rawData[0]));
		}
		catch(Exception e) {
			try {
				this._timeStamp = new SimpleDateFormat("dd/mm/YY HH:mm:ss").parse(rawData[0]);
			}
			catch(Exception e1) {
				try {
				this._timeStamp = new SimpleDateFormat("YY-mm-dd HH:mm:ss").parse(rawData[0]);
				}
				catch(Exception o2) {
					try {
						this._timeStamp = new SimpleDateFormat("dd/mm/YYYY HH:mm").parse(rawData[0]);
						}
					catch(Exception o3) {
					throw new Exception("o3"+o3.getMessage());
					}
				}
			}
		// -------------------------------------------------------------------- //
			this._wiggleScanner = new WiggleScanner();
			this._wiggleScanner.setModel(rawData[1]);
			this._wiggleScanner.setDevice(rawData[1]);
			this._location = new Point3d(rawData[2].contentEquals("")? Integer.MIN_VALUE:Double.parseDouble(rawData[2]),rawData[3].contentEquals("")? Integer.MIN_VALUE:Double.parseDouble(rawData[3]),rawData[4].contentEquals("")? Integer.MIN_VALUE:Double.parseDouble(rawData[4]));
			this._wifiNetworks = rawData[5];
			for(int i = 6; i<rawData.length; i+=4) {
				Double currentChannel = rawData[i+2].contentEquals("")? Integer.MIN_VALUE:Double.parseDouble(rawData[i+2]);
				this._wifis.addWifi(new Wifi(rawData[i+1], rawData[i], null, SIMPLE_DATE_FORMATER.format(this._timeStamp), currentChannel,Double.parseDouble(rawData[i+3]),this._location.y, this._location.x, this._location.z, null, null, this._wiggleScanner));
			}
			this._macList = new ArrayList<Mac>();
			Mac previous = this._wifis.getArrayList().get(0).getMac();
			for(Wifi wifi: this._wifis.getArrayList().subList(1, this._wifis.getArrayList().size())) {
				if(!wifi.getMac().equalTo(previous)) {
					this._macList.add(wifi.getMac());
					previous = wifi.getMac();
				}
			}
		}
	}
	
	// ---------------------------------------------- Geo ----------------------------------------------------:


	public DBWifiTime fetchSimilarWifiTimeStampsFromDB(DBWifiTime db ) throws Exception {
		db.sortBySimilarityTo(this);// sort by similarity
		ArrayList<DBWifiTimeStamp> resultList = new ArrayList<DBWifiTimeStamp>();
		for(int i=0; i<db.getWifiTimeStamps().size() && i<Geo.MIN_NUM_OF_ELEMENTS_FOR_SIMILARITY;i++) {// fetch only first number of timestamps needed for similarity
			resultList.add(db.getWifiTimeStamps().get(i));
		}
		DBWifiTime result = new DBWifiTime(resultList);
		return result;
	}
	public double similarityTo(DBWifiTimeStamp other) {
		double result = 0;
		
		return result;
	}
	// -------------------------------------------------------------------------------------------------------:
	public boolean hasMac(Mac mac) {
		for(Wifi wifi: this._wifis.getArrayList()) {
			if(wifi.getMac().equalTo(mac))
				return true;
		}
		return false;
	}
	public int numOfSameWifis(DBWifiTimeStamp other) {
		int result = 0;

			
		return result;
	}
	public boolean hasLocation() {
		return (this.getScanLocation().x!=Main.NO_INPUT_INT&&this.getScanLocation().y!=Main.NO_INPUT_INT&&this.getScanLocation().z!=Main.NO_INPUT_INT);
	}

	// -------------------------------------------- Getters --------------------------------------------------:
	public WifiList getWifis() {
		return this._wifis;
	}
	public ArrayList<Wifi> getWifisList(){
		return this._wifis.getArrayList();
	}
	public Point3d getScanLocation() {
		return this._location;
	}
	public ArrayList<Mac> getMacList(){
		if(this._macList==null || this._macList.size()==0) {
			for(Wifi wifi: this.getWifisList()) {
				this._macList.add(wifi.getMac());
			}
		}
		return this._macList;
	}
	
	public ArrayList<Mac> generateMacList(){
		this._macList = new ArrayList<Mac>();
		ArrayList<Mac> result = new ArrayList<Mac>();
		for(Wifi wifi: this._wifis.getArrayList()) {
			this._macList.add(wifi.getMac());
			result.add(wifi.getMac());
		}
		return result;
	}
	public WiggleScanner getWiggleScanner() {
		return this._wiggleScanner;
	}
	public void setScanLocation(Point3d point) {
		this._location = point;
	}

	
	// ----------------------------------------------- Prints ------------------------------------------------:
	public String getTimeStamp() {
		return SIMPLE_DATE_FORMATER.format(this._timeStamp);
	}
	public Date getTimeStampDate() {
		return this._timeStamp;
	}
	public void printWifiTimeStamp(DBWifiTimeStamp similarityTo) {
		if(similarityTo!=null) {
			this._wifis.printWifiList("TimeStamp : "+SIMPLE_DATE_FORMATER.format(this._timeStamp)+" | ");
			System.out.println("Similarity : "+Geo.similarityBetween(this, similarityTo));
			similarityTo.printDBWifiTimeStamp();
		}
		else
			this.getWifis().printWifiList("TimeStamp : "+SIMPLE_DATE_FORMATER.format(this._timeStamp));
	}
	public void printWifiTimeStamp() {
		this.printWifiTimeStamp(null);
	}
	public void printDBWifiTimeStamp() {
		String result="";
		// TimeStamp
		result +=this.getTimeStamp()+CsvFile.COMMA_DELIMITER;
		// Model
    	result +=this._wiggleScanner.getModel()+this._wiggleScanner.getDevice()+CsvFile.COMMA_DELIMITER;
    	// Lat,Lon,Alt
    	result +=String.valueOf(this.getScanLocation().y)+CsvFile.COMMA_DELIMITER;
    	result +=String.valueOf(this.getScanLocation().x)+CsvFile.COMMA_DELIMITER;
    	result +=String.valueOf(this.getScanLocation().z)+CsvFile.COMMA_DELIMITER;

    	// #WifiNetworks
    	result+="N/A"+CsvFile.COMMA_DELIMITER;

        // print Wifis of Timestamp
        for(Wifi wifi: this.getWifis().getArrayList()) {
        	result+=String.valueOf(wifi.getSsid())+CsvFile.COMMA_DELIMITER;
        	result+=String.valueOf(wifi.getMac())+CsvFile.COMMA_DELIMITER;
        
        	if(wifi.getChannel()==Main.NO_INPUT_INT) {
	        	result += String.valueOf("N/A")+CsvFile.COMMA_DELIMITER;
        	}
	        else {
	        	result+=String.valueOf(wifi.getMac())+CsvFile.COMMA_DELIMITER;
	        }
        	if(wifi.getChannel()==Main.NO_INPUT_INT) {
	        	result += String.valueOf("N/A")+CsvFile.COMMA_DELIMITER;
        	}
	        else {
	        	result+=String.valueOf(wifi.getMac())+CsvFile.COMMA_DELIMITER;
	        }
    	}
        
        System.out.println(result+"\n");
	}

	
	// ----------------------------------------- Sorts & Comparators -----------------------------------------:

	static class SimilarityComparator implements Comparator<DBWifiTimeStamp>{
		DBWifiTimeStamp _dbWifiTimeStamp;
		public SimilarityComparator(DBWifiTimeStamp wifiTimeStamp) throws Exception {
			this._dbWifiTimeStamp = wifiTimeStamp;
		}
		@Override
		public int compare(DBWifiTimeStamp o1, DBWifiTimeStamp o2) {
			if(Geo.similarityBetween(this._dbWifiTimeStamp, o1)>Geo.similarityBetween(this._dbWifiTimeStamp, o2))
				return 1;
			if(Geo.similarityBetween(this._dbWifiTimeStamp, o1)==Geo.similarityBetween(this._dbWifiTimeStamp, o2))
				return 0;
			else
				return -1;
		}
	}
	@Override
	public int compareTo(DBWifiTimeStamp o) {
		return (this._timeStamp.compareTo(o._timeStamp));
	}



}
