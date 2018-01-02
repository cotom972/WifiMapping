package projectMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.vecmath.Point3d;

public class Geo implements Comparable<Geo> {
	
	private double _similarity;
	
	static final int MAX_NUM_OF_ELEMENTS_FOR_AVERAGE = 10;
	static final int MIN_NUM_OF_ELEMENTS_FOR_SIMILARITY = 1;
	private static final int ALGO2_POWER = 2;
	private static final double ALGO2_NORM = 10000;
	private static final double ALGO2_SIG_DIF = 0.4;
	static final double ALGO2_MIN_DIF = 3;
	private static final double ALGO2_NO_SIGNAL = -120;
	private static final double ALGO2_DIF_NO_SIG = 100;
	
	//------------------------ Algo 1: estimate scan location by Mac's signals --------------------------//
	/**
	 * Estimates 
	 * @param wifis List of same MAC address wifi points.
	 * @param mac MAC address
	 * @return Point3d averaged according to algorithm.
	 * @throws Exception
	 */
	public static Point3d estimateMacLocation(ArrayList<Wifi> wifis, String mac) throws Exception {
		
		// List and sort relevant Wifis from list by mac, and by signal - strongest first.
		WifiList list = new WifiList(wifis);
		ArrayList<Wifi> relevant = new ArrayList<Wifi>();
		list.sortBySignal();
		if(mac==null || mac.contentEquals("") || mac.contentEquals(Main.NO_INPUT_STRING)) {
			for(int i=0; i<MAX_NUM_OF_ELEMENTS_FOR_AVERAGE && i<wifis.size();i++) {
				relevant.add(list.getArrayList().get(i));
			}
		}
		else{
			for(Wifi wifi: list.getArrayList()) {
				if(wifi.getMac().getAddress().contentEquals(mac)&& wifi.hasLocation()) {
					relevant.add(wifi);
				}
			}
		}
		
		// Average wifi's by their location relative to their signal's strength.
		double wLatSum = 0;
		double wLonSum = 0;
		double wAltSum = 0;
		double weightSum =0;
		for(int i=0; i<relevant.size() && i< MAX_NUM_OF_ELEMENTS_FOR_AVERAGE; i++) {
			double currentWeight = relevant.get(i).getSignalWeight();
			double wLat = currentWeight*relevant.get(i).getLat();
			double wLon = currentWeight*relevant.get(i).getLon();
			double wAlt = currentWeight*relevant.get(i).getAlt();
			wLatSum += wLat;
			wLonSum += wLon;
			wAltSum += wAlt;
			weightSum += currentWeight;
		}
		// Resulted average:
		Point3d result = new Point3d(wLonSum/weightSum,wLatSum/weightSum,wAltSum/weightSum);
		return result;
	}
	/**
	 * Overloads estimateMacLocation for use with a WifiList object.
	 * @param wifis List of same MAC address wifi points.
	 * @param mac MAC address
	 * @return Point3d averaged according to algorithm.
	 * @throws Exception
	 */
	public static Point3d estimateMacLocation(WifiList wifis, String mac) throws Exception {
		return estimateMacLocation(wifis.getArrayList(), mac);
	}
		
	//------------------------------------ Algo 2 ----------------------------------------//
	public static Point3d estimateMacLocationWith(Wifi originalWifi, WifiList externalList) throws Exception{
		
		
		Point3d result = new Point3d();								
		
		// Make a copy of external wifi list to operate on:
		WifiList externalListMac = new WifiList(externalList);		
		externalList.printWifiList("External List");
		externalListMac.printWifiList("Inner mac list");
		// Filter out all non relevant MACs:
		externalListMac.filterMac(originalWifi.getMac());		
		externalListMac.printWifiList("Inner mac list after filtermac:");
		// Sort by difference from original wifi:
		Collections.sort(externalListMac.getArrayList(),new SimilarityComparator(originalWifi));
		externalListMac.printWifiList("Inner mac list after Sort:");
		// Use just first 10 (final MAX...) wifis to use for estimating location:
		if(externalListMac.getArrayList().size()>MAX_NUM_OF_ELEMENTS_FOR_AVERAGE) {
			externalListMac.getArrayList().subList(MAX_NUM_OF_ELEMENTS_FOR_AVERAGE, externalListMac.getSize()).clear();
		}
		externalListMac.printWifiList("Inner mac list after cut off non relevant macs:");
		
		// Estimate location by externalListMac wifis:
		
		
		
		
		return result;
	}
	public static double similarityOfMacInList(Wifi wifiAnchor,WifiList externalList) throws Exception {
		double result = 1;
		
		// Make a copy of external wifi list to operate on:
		WifiList externalListMac = new WifiList(externalList);		
		externalList.printWifiList("External List");
		externalListMac.printWifiList("Inner mac list");
		// Filter out all non relevant MACs:
		externalListMac.filterMac(wifiAnchor.getMac());		
		externalListMac.printWifiList("Inner mac list after filtermac:");
		// Sort by difference from original wifi:
		Collections.sort(externalListMac.getArrayList(),new SimilarityComparator(wifiAnchor));
		externalListMac.printWifiList("Inner mac list after Sort:");
		// Use just first 10 (final MAX...) wifis to use for estimating location:
		if(externalListMac.getArrayList().size()>MAX_NUM_OF_ELEMENTS_FOR_AVERAGE) {
			externalListMac.getArrayList().subList(MAX_NUM_OF_ELEMENTS_FOR_AVERAGE, externalListMac.getSize()).clear();
		}
		externalListMac.printWifiList("Inner mac list after cut off non relevant macs:");
//		
//		for(Wifi wifi: externalListMac.getArrayList()) {
//			result = result * wifiSimilarity(wifi, wifiAnchor);
//		}
		return result;
	}
	
	//------------------------------------ Algo 2 ----------------------------------------//
	/**
	 * Caclulates similarity between 2 WifiTimeStamps / DBrows.
	 * 1. To compare 2 DBWifiTimeStamps there must be a minimum of same wifis in both TimeStamps.
	 * Minimum of wifis for comparision and 'similarity' calculation is set at the final int variable in the Geo.class: MIN_NUM_OF_ELEMENTS_FOR_SIMILARITY.
	 * 2. If there are enough wifis for calculation, calculate each wifi's single similarity to the one in o2.
	 * 3. multilply all results to a 1 resulted double representing the "row" / 'DBWifiTimeStamp' similarity between o1 and o2.
	 * @param o1 First DBWifiTimeStamp
	 * @param o2 Second DBWifiTimeStamp to compare to.
	 * @return	Double, representing the "row" / DBWifiTimeStamp similarity between o1 and o2.
	 */
	public static double similarityBetween(DBWifiTimeStamp o1, DBWifiTimeStamp o2) {

		int similarMacs = 0;
		for(Mac mac: o1.generateMacList()) {					// 1. Check if there are enough similar macs in o2 of o1's macs for similarity:
			if(o2.generateMacList().contains(mac))
				similarMacs ++;
		}
		if(similarMacs<Geo.MIN_NUM_OF_ELEMENTS_FOR_SIMILARITY)
			return Main.SIMILARITY_MIN_RANGE;	
		
		double result =1;
		// 2. For each mac in o1, calculate its similarity relative to the one in o2
		for(Wifi wifi: o1.getWifis().getArrayList()) {  //	  Multiply all similarities together to calculate total row similarity.
			for(Wifi secondWifi: o2.getWifis().getArrayList()) {
				if(wifi.sameMacOf(secondWifi)) {
					result = result*singleWifiSimilarity(wifi, secondWifi);
				}
			}
		}
		return result;
	}	
	public static Point3d estimateWifiTimeStampLocations(DBWifiTime db, DBWifiTimeStamp source) throws Exception {
	
		DBWifiTime relevantDB = db.fetchSimilarWifiTimeStampsFromDB(source); // Fetch similar rows
		if(relevantDB==null || relevantDB.getWifiTimeStamps().size()==0)
			return new Point3d(Main.NO_INPUT_INT,Main.NO_INPUT_INT,Main.NO_INPUT_INT);
		Point3d result;
		double lat = 0;
		double lon = 0;
		double alt = 0;
		double weight = 0;
		for(DBWifiTimeStamp timeStamp: relevantDB.getWifiTimeStamps()) {
			if(timeStamp.hasLocation()) {		 // Calculate using first [maximum number of stamps neede for calculation - set at the main.class].
 				double currentWeight = similarityBetween(source,timeStamp);
				weight += currentWeight;
				lat += timeStamp.getScanLocation().y*currentWeight;
				lon += timeStamp.getScanLocation().x*currentWeight;
				alt += timeStamp.getScanLocation().z*currentWeight;
			}
		}
		result = new Point3d(lon/weight,lat/weight,alt/weight);
		return result;
	}
	
	//-------------------------------- Algo 2 Extended -----------------------------------//
	/**
	 * Caculates similarity between 2 given Wifis.
	 * @param o1 First wifi to compare.
	 * @param o2 Second Wifi to compare to.
	 * @return A double representing the similarity between 2 Wifis.
	 */
	public static double singleWifiSimilarity(Wifi o1, Wifi o2) {
		double result = 1;
		double difSignal =  o2.getSignal().getStrength()==Main.ALGO2_NO_SIGNAL ? Main.ALGO2_DIF_NO_SIG:o1.getSignal().getStrength() - o2.getSignal().getStrength();
		difSignal = Math.abs(difSignal);
		if(difSignal==0)
			return Main.SIMILARITY_MAX_RANGE;
		result = (ALGO2_NORM/(Math.pow(difSignal, ALGO2_SIG_DIF)*Math.pow(o1.getSignal().getStrength(), ALGO2_POWER)));
		return result;
	}
	public static ArrayList<Wifi> sortByDiff(WifiList wifis, Wifi originalWifi) throws Exception {
		ArrayList<Wifi> result = new ArrayList<Wifi>();
		for(Wifi wifi: wifis.getArrayList()) {	// Copy ArrayList<Wifi> to one that will be sorted.
			result.add(new Wifi(wifi));
		}
		Collections.sort(result,new SimilarityComparator(originalWifi));
		return result;
	}
	
	//------------------------------- Other Class Functions ------------------------------//
	@Override
	public int compareTo(Geo o) {
		if(this._similarity>o._similarity)
			return 1;
		else if(this._similarity==o._similarity)
			return 0;
		else
			return -1;
	}
	public static double getWeight(Signal sig) {
			return (1/(sig.getStrength()*sig.getStrength()));
	}

	//------------------------------------ Comparators -----------------------------------//
	static class SimilarityComparator implements Comparator<Wifi>{
		private Wifi _wifi;
		
		public SimilarityComparator(Wifi wifi) {
			this._wifi = wifi;
		}
		
		public int compare(Wifi o1, Wifi o2) {
			double difo1 = singleWifiSimilarity(this._wifi, o1);
			double difo2 = singleWifiSimilarity(this._wifi, o2);
			int result = difo1>difo2 ? 1: difo1==difo2 ? 0: -1;
			return result;
		}
	}




}
