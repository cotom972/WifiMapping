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
	private int size;
	
	// ------------------------------------------ Constructors -----------------------------------------------:
	/**
	 * WifiList constructor - Constructs a list of wifis from the given ArrayList of wifis.
	 * @param wifis list of wifis.
	 */
	public WifiList(ArrayList<Wifi> wifis) {
		this.WifiList = new ArrayList<Wifi>(wifis);
		this.size = this.WifiList.size();
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
	
	// --------------------------------- Filter and manipluation functions : ---------------------------------:
	
	/** ---------- Filter Signal  --------**/
	/**
	 * Return a list of Wifi's with a signal above given bound.
	 * @param signalLow - Minimum signal requested to be on list.
	 * @param signalHigh - Maximum signal requested to be on list.
	 */
	public void filterSignal(double signalLow,double signalHigh){
		
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		
		// Add wifis with signals stronger/equal to given signal range.
		for(Wifi wifi: this.WifiList) {		
			if(wifi.getRssi()>=signalLow && wifi.getRssi()<=signalHigh)
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

	/** ---------- Filter Range  ---------**/
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

//			System.out.print(formatter.format(distance));
//			distance = Math.ceil(Double.parseDouble(formatter.format(distance)));
//			
//			System.out.println(formatter.format(distance)+"    "+distance+"   "+wifi.getPoint3d().distanceSquared(anchor)+"   "+range+(distance <=range));
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
	
	/** ------------- Date ------------- **/
	/**
	 * Filters out all wifis in list that aren't in the given time range given.
	 * @param from - From this given date.
	 * @param to - Until this given date. 
	 */
	public void filterDate( Date from, Date to){
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		for(Wifi wifi: this.WifiList) {		
			if(wifi.getDate().after(from) && wifi.getDate().before(to))
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
	
	/** ------------- MAC -------------- **/
	/**
	 * Filters from a Wifi ArrayList the ones with the requested MAC address.
	 * @param mac - MAC address that is relevant.
	 */
	public void filterMac(String mac){
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		for(Wifi wifi: this.WifiList) {		
			if(wifi.getMac().getAddress().equalsIgnoreCase(mac))
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
	
	// -------------------------------------------------------------------------------------------------------:

	/**
	 * Prints MAC address and SSID of all wifis in list
	 * @param header - Header
	 */
	public void printWifiList(String header) {
		System.out.println("\n_________________________________________________\n");
		if(header!=null) 
		System.out.println(header);
		else
			System.out.println("		Wifi List");

		System.out.println("-------------------------------------------------");
		int counter = 0;
		String seperator = "  ";
		for(Wifi wifi: this.WifiList) {
			counter++;
			seperator = counter>9 ? " ":counter>99 ? "":"  ";
			System.out.println(counter+seperator+"| "+wifi.getRssi()+" | "+wifi.getMac()+" | "+wifi.getSsid());
		}
		System.out.println("-------------------------------------------------\n");

	}
	/**
	 * Prints MAC address and SSID of all wifis in list 
	 */
	public void printWifiList() {
		this.printWifiList(null);
	}
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
				if(wifi.getRssi()>result.get(result.size()-1).getRssi()) {
					result.remove(result.size()-1);
					result.add(wifi);
				}
				
			}
		}
		
		this.WifiList = result;
	}
	}
	/** 
	 * Comparator to sort by MAC address.
	 * @author NT
	 *
	 */
	static class MacComparator implements Comparator<Wifi>{
		public int compare(Wifi o1, Wifi o2) {
			int result =  o1.getMac().getAddress().compareTo(o2.getMac().getAddress());
			if(result!=0) 
				return result;
			else
				return Double.compare(o1.getRssi(), o2.getRssi());
		}
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
