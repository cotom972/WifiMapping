package projectMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.vecmath.Point3d;

import com.sun.xml.internal.ws.util.StringUtils;
/**
 * @author Tom
 * This class represents a list of wifis, "held" as Wifi objects in an ArrayList<Wifi>.
 */
public class WifiList {
	
	ArrayList<Wifi> WifiList;
	
	
	// ------------------------------------------ Constructors -----------------------------------------------:
	/**
	 * WifiList constructor - Constructs a list of wifis from the given ArrayList of wifis.
	 * @param wifis list of wifis.
	 */
	public WifiList(ArrayList<Wifi> wifis) {
		this.WifiList = new ArrayList<Wifi>(wifis);
	}
	/**
	 * Copy Constructor.
	 * @param other WifiList to be copied from.
	 * @throws Exception
	 */
	public WifiList(WifiList other) throws Exception {
		this.WifiList = new ArrayList<Wifi>(other.WifiList.size());
		for(Wifi wifi: other.WifiList) {
			this.WifiList.add(new Wifi(wifi));
		}
	}
	
	// --------------------------------- Filter and manipluation functions : ---------------------------------:
	
	/** ---------- Filter Signal  --------**/
	/**
	 * Return a list of Wifi's with a signal above given bound.
	 * @param wifis - List of all wifis.
	 * @param signalLow - Minimum signal requested to be on list.
	 * @param signalHigh - Maximum signal requested to be on list.
	 * @return - Array
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
	 * @param wifis - List of all wifis.
	 * @param signalLow - Minimum signal requested to be on list.
	 * @return - Array
	 */
	public void filterSignal(double signalLow){
		this.filterSignal(signalLow,0);
	}

	/** ---------- Filter Range  ---------**/
	/**
	 * Filters out wifis that are not in range from given point.
	 * @param wifis - List of wifi's to filter out from.
	 * @param lat - Lat of anchor/point.
	 * @param lon - Lon of anchor/point.
	 * @param alt - Alt of anchor/point.
	 * @param range - range in meters of maximum distance to count in the wifis.
	 * @return ArrayList
	 */
	public void filterRange(double lon,double lat,double alt, int range){
		
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		Point3d anchor = new Point3d(lon, lat, alt);
		
		// Add wifis in range to new list:
		for(Wifi wifi: this.WifiList) {		
			if(wifi.getPoint3d().distance(anchor)<=range)
				result.add(wifi);	
		}
		this.WifiList = result;
	}
	/**
	 * Overides filterRange() function to be used with a 'Point3d' object instead of specified lat/lon/alt values.
	 * @param wifis - List of wifi's to filter out from.
	 * @param point - Point3d object representing current location to measure from.
	 * @param range - range in meters of maximum distance to count in the wifis.
	 * @return ArrayList
	 */
	public void filterRange(Point3d point, int range){
		this.filterRange(point.y,point.x, point.z, range);
	}
	
	/** ------------- Date ------------- **/
	/**
	 * Filters out all wifis in list that aren't in the given time range given.
	 * @param from - From this given date.
	 * @param to - Until this given date.
	 * @return - ArrayList 
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
	 * @param wifis - List of all wifis
	 * @param mac - MAC address that is relevant.
	 * @return - Returns an ArrayList of wifis with given MAC address.
	 */
	public void filterMac(String mac){
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		for(Wifi wifi: this.WifiList) {		
			if(wifi.getMac().getAddress().equalsIgnoreCase(mac))
				result.add(wifi);	
		}
		this.WifiList = result;
	}
	/**
	 * Filters out duplicate wifi MAC address in wifi list. In case of duplicate MAC address wifis - keeps the one with the strongest signal.
	 * @param wifis - List of wifis to 
	 * @return List of wifis, representing the ones with the strongest signal in case there were duplicates.
	 */
	public void filterOutMacDuplicates(){
		ArrayList<Wifi> result = new ArrayList<Wifi>();	
		
		for(Wifi wifi: this.WifiList) {
			int[] macIndexes = this.getAllIndexesWithMac(wifi.getMac().getAddress());
			int indexOfHighestSignal = 0;
			for(int i=0; i<macIndexes.length; i++) {
				if(this.WifiList.get(macIndexes[i]).getRssi()>this.WifiList.get(macIndexes[indexOfHighestSignal]).getRssi()) {
					indexOfHighestSignal = macIndexes[i];
				}
			}
			if(!result.contains(this.WifiList.get(macIndexes[indexOfHighestSignal]))) {
			result.add(this.WifiList.get(macIndexes[indexOfHighestSignal]));
			}
		}
		this.WifiList = result;
	}
	
	// -------------------------------------------------------------------------------------------------------:

	/**
	 * Get all indexes from Wifilist holding the same MAC address.
	 * @param wifis - List of wifis to search in.
	 * @param mac - MAC address to look for if there are any duplicates in list.
	 * @return - Int[] array holding the indexes from 'wifiList' with same MAC address.
	 */
	public int[] getAllIndexesWithMac(String mac) {
		// Count how many appearence of MAC address:
		int count=0;
		for(Wifi wifi: this.WifiList) {
			System.out.println(wifi.getMac());
			System.out.println(mac);
			System.out.println(wifi.getMac().getAddress().equalsIgnoreCase(mac));
			if(wifi.getMac().getAddress().equalsIgnoreCase(mac))
				count++;
		}
		// Store in int array:
		int[] indexes = new int[count];
		for(Wifi wifi:this.WifiList) {
			if(wifi.getMac().getAddress().equalsIgnoreCase(mac)){
				indexes[indexes.length-count] = this.WifiList.indexOf(wifi);
				count--;
			}
		}
		return indexes;
	}
	/**
	 * Prints MAC address and SSID of all wifis in list
	 * @param header - Header
	 * @param wifis - Wifi list to print out.
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

}
