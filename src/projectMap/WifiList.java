package projectMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.vecmath.Point3d;

/**
 * @author Tom
 * This class represents a list of wifis, "held" as Wifi objects in an ArrayList<Wifi>.
 */
public class WifiList {
	
	private ArrayList<Wifi> WifiList;
	private ArrayList<Date> _listDates;

	private int size = 0;
	private WiggleScanner _wiggleScanner;
	public enum GeoVariables {
	    Lat, Lon, Alt, Difference,
	}
	// ------------------------------------------ Constructors -----------------------------------------------:
	public WifiList() {
		this.WifiList = new ArrayList<Wifi>();
		this.size = 0;			
		
	}
	/**
	 * WifiList constructor - Constructs a list of wifis from the given ArrayList of wifis.
	 * @param wifis list of wifis.
	 */
	public WifiList(ArrayList<Wifi> wifis) {
		this.WifiList = new ArrayList<Wifi>(wifis);
		this.size = this.WifiList.size();
		this.generateDates();
	}
	/**
	 * Copy Constructor.
	 * @param other WifiList to be copied from.
	 */
	public WifiList(WifiList other) throws Exception {
		this.WifiList = new ArrayList<Wifi>(other.WifiList.size());
		for(Wifi wifi: other.WifiList) {
			this.WifiList.add(new Wifi(wifi));
		}
		this.size = this.WifiList.size();
	}
	
	// ---------------------------------------------- DB  ----------------------------------------------------:
	

	// --------------------------------------- Geo Location functions : --------------------------------------:
	public void estimatedMacLocations() {
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		ArrayList<Wifi> tempMacLst = new ArrayList<Wifi>();
		// Sort by Macs
		this.sortByMac();	
	}	
	public void groupByEstimatedMacLocations() throws Exception {
		
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		this.sortByMac();
		
		for(int i=0; i<this.WifiList.size(); i++) {
			Point3d estimatedLocation = Geo.estimateMacLocation(this.WifiList, this.WifiList.get(i).getMac().getAddress());
			result.add(this.WifiList.get(i));
			result.get(result.size()-1).setPoint3d(estimatedLocation);
			i = i+this.getNumOfMac(this.WifiList.get(i).getMac());
		}

		this.WifiList = result;
	}

	
// ------------------------------------------- Sort functions  -------------------------------------------:
	/** 
	 * Sorts list by signal.
	 */
	public void sortBySignal() {
		Collections.sort(this.WifiList);
		Collections.reverse(this.WifiList);
	}
	/**
	 * Sorts wifis by mac.
	 */
	public void sortByMac() {
		Collections.sort(this.WifiList, new MacComparator());
	}
	/**
	 * Sorts list by Date.
	 */
	public void sortByDate() {
		Collections.sort(this.WifiList,new DateComparator());
	}
    //	------------ Comparators ------------:
	/** 
	 * Comparator to sort by MAC address.
	 */
	static class MacComparator implements Comparator<Wifi>{
		public int compare(Wifi o1, Wifi o2) {
			if(o2.getMac().getAddress().contentEquals(Main.NO_INPUT_STRING))
				return 1;
			else if(o1.getMac().getAddress().contentEquals(Main.NO_INPUT_STRING))
				return -1;
			else {
			int result =  o1.getMac().getAddress().compareTo(o2.getMac().getAddress());
			if(result!=0) 
				return result;
			else
				return Double.compare(o2.getSignal().getStrength(), o1.getSignal().getStrength());
			}
		}
	}
	/** 
	 * Comparator to sort by Date.
	 */
	static class DateComparator implements Comparator<Wifi>{
		public int compare(Wifi o1, Wifi o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	}

// ------------------------------------------ Filter functions : -----------------------------------------:
	// Signal:
	/**
	 * Return a list of Wifi's with a signal above given bound.
	 * @param signalLow - Minimum signal requested to be on list.
	 * @param signalHigh - Maximum signal requested to be on list.
	 */
	public void filterSignal(double signalLow,double signalHigh){
		
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		
		// Add wifis with signals stronger/equal to given signal range.
		for(Wifi wifi: this.WifiList) {		
			if(wifi.getSignal().getStrength() >=signalLow && wifi.getSignal().getStrength()<=signalHigh)
				result.add(wifi);	
		}
		this.WifiList = result;
	}	
	/**
	 * Overloads filterSignal() function for use just with a "lower" bound.
	 * @param signalLow - Minimum signal requested to be on list.
	 */
	public void filterSignal(double signalLow){
		this.filterSignal(signalLow,0);
	}
	
	// Range:
	/**
	 * Filters out wifis that are not in range from given point.
	 * @param lat - Lat of anchor/point.
	 * @param lon - Lon of anchor/point.
	 * @param alt - Alt of anchor/point.
	 * @param range - range in meters of maximum distance to count in the wifis.
	 */
	public void filterRange(double lon,double lat,double alt, int range){
		
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		Point3d anchor = new Point3d(lon, lat, alt);

		// Add wifis in range to new list:
		for(Wifi wifi: this.WifiList) {	
			BigDecimal bd = new BigDecimal(wifi.getPoint3d().distanceSquared(anchor));	
			NumberFormat formatter = new DecimalFormat("#.###");
			
			double distance = distance(anchor.y, anchor.x, anchor.z, wifi.getPoint3d().y, wifi.getPoint3d().x, wifi.getPoint3d().z);
			System.out.println("distancemethod:   "+distance+"    "+wifi.getPoint3d().distanceSquared(anchor)+" After format:  "+formatter.format(bd.doubleValue())+"After in value:  "+bd.intValue());

			if(distance<=(double)range)
				result.add(wifi);	
		}
		this.WifiList = result;
	}
	/**
	 * Overloads filterRange() function to be used with a 'Point3d' object instead of specified lat/lon/alt values.
	 * @param point - Point3d object representing current location to measure from.
	 * @param range - range in meters of maximum distance to count in the wifis.
	 */
	public void filterRange(Point3d point, int range){
		this.filterRange(point.y,point.x, point.z, range);
	}
	/**
	 * Filter out wifis in range radius given in meters from a given "anchor" / point.
	 * @param anchor The point to measure the distance/range from
	 * @param range Distance in meters.
	 */
	public void filterOutRange(Point3d anchor, int range) {
		
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		
		
		// Add wifis in range to new list:
		for(Wifi wifi: this.WifiList) {		
			double distance = distance(anchor.y, anchor.x, anchor.z, wifi.getPoint3d().y, wifi.getPoint3d().x, wifi.getPoint3d().z);
			if(distance>=(double)range)
				result.add(wifi);	
		}
		this.WifiList = result;
	}
	
	// Time:
	/**
	 * Filters out all wifis in list that aren't in the given time range given.
	 * @param from - From this given date.
	 * @param to - Until this given date. 
	 */
	public void filterDate(Date from, Date to){
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		for(Wifi wifi: this.WifiList) {		
			if(wifi.getDate().compareTo(from)>=0 && wifi.getDate().compareTo(to)<=0)
				result.add(wifi);	
		}
		this.WifiList = result;
	}
	/**
	 * Overloads filterDate() function.
	 * @param wifis - List of all wifis.
	 * @param days - How many days ago are relevant to list wifis.
	 * @return - ArrayList
	 */
	public static ArrayList<Wifi> filterDateLastNumOfDays(WifiList wifis, int days){
		
		// Calculate Date object acording to 'days' before.
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		Date current = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.add(Calendar.DATE, days);
		Date daysAgo = cal.getTime();
	
		for(Wifi wifi: wifis.WifiList) {
			if(wifi.getDate().after(daysAgo))
				result.add(wifi);
		}
		return result;
	}
	
	// Mac:
	/**
	 * Filters from a Wifi ArrayList the ones with the requested MAC address.
	 * @param mac - MAC address that is relevant.
	 */
	public void filterMac(Mac mac){
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		for(Wifi wifi: this.WifiList) {		
			if(wifi.getMac().getAddress().equalsIgnoreCase(mac.getAddress()))
				result.add(wifi);	
		}
		this.WifiList = result;
	}
	
	// -------------------------------------------- Getters --------------------------------------------------:
	/**
	 * 
	 * @return Gets the size of list.
	 */
	public int getSize() {
		return this.size;
	}
	/**
	 * Gets the ArrayList of WifiList obj.
	 * @return Gets the ArrayList of WifiList obj.
	 */
	public ArrayList<Wifi> getArrayList(){
		return this.WifiList;
	}
	/**
	 * 
	 * @param mac MAC of array requested.
	 * @return Return a Wifi[] object, containing all Wifi with mac requested from current list.
	 */
	public Wifi[] getMacWifis(Mac mac) {
		Wifi[] macWifis = new Wifi[this.getNumOfMac(mac)];
		int arrayIndex = 0;
		for(Wifi wifi: this.WifiList) {
			if(wifi.hasMac(mac)) {
				macWifis[arrayIndex] = wifi;
				arrayIndex++;
			}
		}
		return macWifis;
	}
	/**
	 * Gives an array of signals 
	 * @param mac
	 * @return
	 */
	public Signal[] getSignalsOfMac(Mac mac) {
		Signal[] result = new Signal[this.getNumOfMac(mac)];
		int arrayIndex = 0;
		for(Wifi wifi: this.WifiList) {
			if(wifi.hasMac(mac)) {
				result[arrayIndex++] = wifi.getSignal();
			}
		}
		return result;
	}
	/**
	 * Returns number of MAC appearence in list.
	 * @param mac MAC to count.
	 * @return  Returns number of MAC appearence in list.
	 */
	public int getNumOfMac(Mac mac) {
		int count = 0;
		for(Wifi wifi: this.WifiList) {
			if(wifi.hasMac(mac))
				count ++;
		}
		return count;
	}
	/**
	 * Retrieves an array of MacSignals of all list.
	 * @return Array of MacSignals of all of the wifis in list.
	 * @throws Exception
	 */
	public MacSignal[] getMacSignals() throws Exception {
		MacSignal[] result = new MacSignal[this.size];
		int arrayIndex=0;
		for(Wifi wifi: this.WifiList) {
			result[arrayIndex++] = new MacSignal(wifi.getMac(), wifi.getSignal());
		}
		return result;
	}
	/**
	 * @return An ArrayList of dates that the WifiList holds record of.
	 */
	public void generateDates(){
		ArrayList<Date> result = new ArrayList<Date>();
		ArrayList<Wifi> sorted = new ArrayList<Wifi>(this.WifiList);
		Collections.sort(sorted,new DateComparator());
		
		Date previous = this.WifiList.get(0).getDate();
		result.add(previous);
		if(this.WifiList.size()>1) {
			for(Wifi wifi: this.WifiList.subList(0, this.WifiList.size())) {
				if(wifi.getDate().compareTo(previous)!= 0) {
					result.add(wifi.getDate());
					previous = wifi.getDate();
				}
			}
		}
		this._listDates = result;
	}
	public WiggleScanner get_wiggleScanner() {
		return _wiggleScanner;
	}
	public void set_wiggleScanner(WiggleScanner _wiggleScanner) {
		this._wiggleScanner = _wiggleScanner;
	}
	public ArrayList<Date> get_listDates() {
		return _listDates;
	}
	public void set_listDates(ArrayList<Date> _listDates) {
		this._listDates = _listDates;
	}
	public Wifi getStrongestSignalWifiWithMac(Mac mac) {
		Wifi[] macWifis = this.getMacWifis(mac);
		if(macWifis.length<1)
			return null;
		Wifi strongest = macWifis[0];
		for(int i=1; i<macWifis.length;i++) {
			if(macWifis[i].getSignal().getStrength()>strongest.getSignal().getStrength())
				strongest = macWifis[i];
		}
		return strongest;
	}

	// -------------------------------------------- Prints ---------------------------------------------------:
	/**
	 * Prints MAC address and SSID of all wifis in list
	 * @param header - Header
	 */
	public void printWifiList(String header) {
		this.printWifiList(header, false, null);
	}
	/**
	 * Prints MAC address and SSID of all wifis in list 
	 */
	public void printWifiList() {
		this.printWifiList(null);
	}
	// TODO:
	public void printWifiList(String header, boolean withGeoDiff, Wifi originalWifi) {
		System.out.println("\n_______________________________________________________________________\n");
		if(header!=null) 
		System.out.println(header);
		else
			System.out.println("		Wifi List");

		System.out.println("-----------------------------------------------------------------------");
		int counter = 0;
		String seperator = "  ";
		if(!withGeoDiff) {
			for(Wifi wifi: this.WifiList) {
				counter++;
				seperator = counter>9 ? " ":counter>99 ? "":"  ";
				System.out.println(counter+seperator+" | "+wifi.printDate() + " | "+wifi.getSignal().getStrength()+" | "+wifi.getMac()+" | "+wifi.getSsid());
			}
		}
		else {
			for(Wifi wifi: this.WifiList) {
				double diff = Geo.singleWifiSimilarity(wifi, originalWifi);
				counter++;
				seperator = counter>9 ? " ":counter>99 ? "":"  ";
				System.out.println(counter+seperator+"| "+wifi.getSignal().getStrength()+" | "+wifi.getMac()+" | "+wifi.getSsid()+" | "+diff);
			}
		}
		System.out.println("-----------------------------------------------------------------------\n");

	}

	// -------------------------------------------------------------------------------------------------------:
	public void mergeWithList(WifiList other) throws Exception {
		for(Wifi wifi: other.WifiList) {
			this.addWifi(wifi);
		}
	}
	
	/**
	 * Adds a Wifi to the WifiList. (adds it to the Arraylist and increments size by 1).s
	 * @param wifi	Wifi to add.
	 * @throws Exception Error in Wifi construction.
	 */
	public void addWifi(Wifi wifi) throws Exception {
		this.WifiList.add(new Wifi(wifi));
		this.size++;
	}
	/** 
	 * Filters out dupilcate MAC address from list.
	 */
	public void filterMacDuplicates() {
		this.sortByMac();
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		
		if(this.WifiList.size()<2)
			return;
		else {
			String tmp = this.WifiList.get(0).getMac().getAddress();
			result.add(this.WifiList.get(0));
		for(Wifi wifi: this.WifiList.subList(1, this.WifiList.size())) {
			if(!wifi.getMac().getAddress().equals(tmp)) {
				result.add(wifi);
				tmp = wifi.getMac().getAddress();
			}
			else {
				if(wifi.getSignal().getStrength()>result.get(result.size()-1).getSignal().getStrength()) {
					result.remove(result.size()-1);
					result.add(wifi);
				}
				
			}
		}
		
		this.WifiList = result;
	}
	}
	/**
	 * Removes from list all appearence of MAC given in argument.
	 * @param mac MAC to remove.
	 */
	public void removeAllMacs(Mac mac) {
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		for(Wifi wifi: this.WifiList) {
			if(!wifi.hasMac(mac)) {
				result.add(wifi);
			}
		}
		this.WifiList = result;
	}
	/**
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 * @returns Distance in Meters
	 */
	public static double distance(double lat1,  double lon1,double el1,double lat2,
								  double lon2,  double el2) {

	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    double height = el1 - el2;

	    distance = Math.pow(distance, 2) + Math.pow(height, 2);

	    return Math.sqrt(distance);
	}
}
