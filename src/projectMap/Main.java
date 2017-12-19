package projectMap;

import java.io.File;
import java.util.ArrayList;

import javax.vecmath.Point3d;
 

public class Main {
	
// ----------------------- Paths configuration -----------------------//
private static String inputDir = "C:\\WifiMapping\\ExternalFiles\\Input-Files";
private static String outputDir =  "C:\\WifiMapping\\ExternalFiles\\Resulted-Files";

// ---------------- Top Wifis by signal configuration ----------------//
private static final int MAX_NUM_OF_WIFIS = 10;
private static final String FILE_NAME_TOP_WIFIS = "Top "+MAX_NUM_OF_WIFIS+" Wifis";
private static final String FILE_NAME_FILTER_SIGNAL = "Wifis in signal range from ";
private static final String FILE_NAME_FILTER_RANGE = "Wifis in distance range of ";
private static final String FILE_NAME_FILTER_MAC = "Wifis with MAC Address -  ";
private static final String FILE_NAME_FILTER_MAC_DUPlICATES = "Filtered out MACs Duplicates";



	public static void main(String[] args) throws Exception {
		
	//	----------------------------------------- Read Files ------------------------------------------------:
		File[] filesCsv = FileHandler.readDirFiles(inputDir,".csv");
		
	//	--------------------------------------- List All Wifis ----------------------------------------------:
		WifiList allWifis = new WifiList(CsvFile.csvToWifiList(filesCsv[0]));

	// 	------------------------------------- List top 10 Wifis ---------------------------------------------:
		allWifis.filterMacDuplicates();
		allWifis.sortBySignal();
		KmlFile.createKmlFile(allWifis, outputDir+"\\all Wifis");
		WifiList topTen = new WifiList(new ArrayList<Wifi>(allWifis.WifiList.subList(0, 10)));
		// Test:
		allWifis.printWifiList("filterMacDuplicates, sortBySignal");
		topTen.printWifiList("Top ten Wifis");
		
	//------------------------------------------ Write to CSV ----------------------------------------------:
		CsvFile.writeCsvFile(outputDir+"\\"+FILE_NAME_TOP_WIFIS+".csv", topTen);
		
	//------------------------------------------ Write to KML ----------------------------------------------:
		KmlFile.createKmlFile(topTen,outputDir+"\\"+FILE_NAME_TOP_WIFIS);
		
		
	//----------------------------------------- Filter Functions -------------------------------------------:
		/**------------- Filter by Signal: -------------**/
		int signalLow = -80;
		int signalHigh = -70;
		WifiList filteredBySignal = new WifiList(topTen);
		filteredBySignal.filterSignal(signalLow, signalHigh);
		// Test and write KML:
		filteredBySignal.printWifiList(FILE_NAME_FILTER_SIGNAL+signalLow+" to  "+signalHigh);
		KmlFile.createKmlFile(filteredBySignal, outputDir+"\\"+FILE_NAME_FILTER_SIGNAL+signalLow+" to  "+signalHigh);
		
		/** ------------- Filter by Range: -------------- **/
		int range = 100;
		Point3d currentLocation = new Point3d(34.77270189,32.06148439,46);
		WifiList filteredByRange = new WifiList(topTen);
		filteredByRange.filterRange(currentLocation, range);
		// Test and write KML:
		filteredByRange.printWifiList(FILE_NAME_FILTER_RANGE+"  -  "+range+"m ");
		KmlFile.createKmlFile(filteredByRange, outputDir+"\\"+FILE_NAME_FILTER_RANGE+"  -  "+range+"m ");
		
		/** -------------- Filter by MAC: --------------- **/
		String mac = "60:e3:27:d9:e8:32";
		WifiList filteredByMac = new WifiList(topTen);
		filteredByMac.filterMac(mac);
		// Test and write KML:
		filteredByMac.printWifiList(FILE_NAME_FILTER_MAC+mac);
		KmlFile.createKmlFile(filteredByMac, outputDir+"\\"+FILE_NAME_FILTER_MAC+mac.replace(':', '-'));
				
		/** ------- Filter out duplicate MACs ----------- **/
		WifiList filteredMacDuplicates = new WifiList(topTen);
		filteredMacDuplicates.filterMacDuplicates();
		KmlFile.createKmlFile(filteredMacDuplicates, outputDir+"\\"+FILE_NAME_FILTER_MAC_DUPlICATES);
		filteredMacDuplicates.printWifiList(FILE_NAME_FILTER_MAC_DUPlICATES);
		
	}
	
	
// ------------ Getters for JUnit to test private variables ------------ //
	public static String getInputDir() {
		return inputDir;
	}
	
	public static String outputDir() {
		return outputDir;
	}
	
}




