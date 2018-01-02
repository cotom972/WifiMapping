package projectMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.vecmath.Point3d;

import projectMap.FileHandler.FileReader;
 

public class Main {

// TODO Move configuration variables to a one class holding all conigurations.
// ----------------------- Paths configuration -----------------------//
 static String inputDir = "C:\\WifiMapping\\ExternalFiles\\WifiMapping - Input";
 static String outputDir =  "C:\\WifiMapping\\ExternalFiles\\WifiMapping - Output";
 static String inputTestDir = "C:\\WifiMapping\\ExternalFiles\\Test - Input";
 static String outputTestDir =  "C:\\WifiMapping\\ExternalFiles\\Test - Output";
 static String inputJunitDir = "C:\\WifiMapping\\ExternalFiles\\Junit - Input For Test";
 
// ---------------- In-Class Methods Configurations  ----------------//
// WiggleWifi app configurations:
 static final String[] WIGGLEWIFI_HEADER_FORMAT = {"WigleWifi","appRelease","model","release","device","display","board","brand"};
 static final String[] DBWIFITIME_HEADER_FORMAT = {"Time","ID","Lat","Lon","Alt","#WiFi networks","board","brand"};
 static final String[] HEADER_SINGLE_LIST = {"MAC","SSID","AuthMode","FirstSeen","Channel","RSSI","CurrentLatitude","CurrentLongitude","AltitudeMeters","AccuracyMeters","Type"};
 static final String FILENAME_WIGGLE_STARTSWITH = "WigleWifi";

 // DB
 static final int MAX_NUM_OF_WIFIS_IN_WIFITIMESTAMP = 10;
 static final String FILENAME_DB_STARTSWITH = "DB_";

 // Geo
 static final int SIMILARITY_MAX_RANGE = 100;
 static final int SIMILARITY_MIN_RANGE = 0;
 // -- Similarity:
 static final int MAX_NUM_OF_ELEMENTS_FOR_AVERAGE = 10;
 static final int MIN_NUM_OF_ELEMENTS_FOR_SIMILARITY = 1;
 static final int ALGO2_POWER = 2;
 static final double ALGO2_NORM = 10000;
 static final double ALGO2_SIG_DIF = 0.4;
 static final double ALGO2_MIN_DIF = 3;
 static final double ALGO2_NO_SIGNAL = -120;
 static final double ALGO2_DIF_NO_SIG = 100;

//------------------------ No Input conventions  ------------------------//
 static final int NO_INPUT_INT = Integer.MIN_VALUE;
 static final String NO_INPUT_STRING = "        N/A         ";

// FileNames (Resulted)
 static final String FILE_NAME_TOP_WIFIS = "Top "+MAX_NUM_OF_WIFIS_IN_WIFITIMESTAMP+" Wifis";
 static final String FILE_NAME_FILTER_SIGNAL = "Wifis in signal range from ";
 static final String FILE_NAME_FILTER_RANGE = "Wifis in distance range of ";
 static final String FILE_NAME_FILTER_MAC = "Wifis with MAC Address -  ";
 static final String FILE_NAME_FILTER_MAC_DUPlICATES = "Filtered out MACs Duplicates";


	public static void main(String[] args) throws Exception {
		
	//	-------------------------------------- Estimate Locations from DB -----------------------------------:
		// Read dir's files:
 		File[] noGps = FileHandler.readDirFiles(inputTestDir,FileReader.NOTDBNOTWIGGLE);// Read the missing locations file.
 		File[] dbFiles = FileHandler.readDirFiles(inputTestDir, FileReader.JUSTDB);		// Read DBWifiTime format files (must begin with DB).
 		File[] wigleFiles = FileHandler.readDirFiles(inputTestDir, FileReader.WIGGLE);	// Read raw wigle files.

 		DBWifiTime DBWiggle = CsvFile.readWiggleToDBWifiTime(wigleFiles); 				// Convert raw wiggle files to DBWifiTime.
 		DBWifiTime noGPS = new DBWifiTime(noGps);						  				// Convert noGPS file to DBWifiTime.
 		
 		noGPS.writeToCsv(outputTestDir, "\\No gps before estimated locations.csv");
 		noGPS.estimateMissingLocations(DBWiggle);						  				// Estimate noGPS locations from the generated DBWifiTime of the raw wiggle files.
		noGPS.writeToCsv(outputTestDir, "\\No gps after estimated locations.csv");

	
 /**
  * Tests:
	//	----------------------------------------- Read Files ------------------------------------------------:
		File[] filesCsv = FileHandler.readDirFiles(inputDir,FileReader.CSVS);
		
	//	--------------------------------------- List All Wifis ----------------------------------------------:
		WifiList allWifis = new WifiList(CsvFile.readWiggleToWifiList(filesCsv[0]));
		allWifis.printWifiList();
		allWifis.sortByDate();
		allWifis.printWifiList();
	// 	------------------------------------- List top 10 Wifis ---------------------------------------------:
		allWifis.filterMacDuplicates();
		allWifis.sortBySignal();
		WifiList topTen = new WifiList(new ArrayList<Wifi>(allWifis.getArrayList().subList(0, 10)));
		// Test:
		allWifis.printWifiList("filterMacDuplicates, sortBySignal");
		topTen.printWifiList("Top ten Wifis");
		
	//------------------------------------------ Write to CSV ----------------------------------------------:
		CsvFile.writeSingleWifiListToCsv(outputDir+"\\"+FILE_NAME_TOP_WIFIS+".csv", topTen);
		
	//------------------------------------------ Write to KML ----------------------------------------------:
		KmlFile.createKmlFile(topTen,outputDir+"\\"+FILE_NAME_TOP_WIFIS);
		
	//----------------------------------------- Filter Functions -------------------------------------------:
		//------------- Filter by Signal: -------------
		int signalLow = -80;
		int signalHigh = -70;
		WifiList filteredBySignal = new WifiList(topTen);
		filteredBySignal.filterSignal(signalLow, signalHigh);
		// Test and write KML:
		filteredBySignal.printWifiList(FILE_NAME_FILTER_SIGNAL+signalLow+" to  "+signalHigh);
		KmlFile.createKmlFile(filteredBySignal, outputDir+"\\"+FILE_NAME_FILTER_SIGNAL+signalLow+" to  "+signalHigh);
		
		// ------------- Filter by Range: -------------- 
		int range = 100;
		Point3d currentLocation = new Point3d(34.77270189,32.06148439,46);
		WifiList filteredByRange = new WifiList(topTen);
		filteredByRange.filterRange(currentLocation, range);
		// Test and write KML:
		filteredByRange.printWifiList(FILE_NAME_FILTER_RANGE+"  -  "+range+"m ");
		KmlFile.createKmlFile(filteredByRange, outputDir+"\\"+FILE_NAME_FILTER_RANGE+"  -  "+range+"m ");
		
		// -------------- Filter by MAC: --------------- 
		String mac = "60:e3:27:d9:e8:32";
		Mac testMac = new Mac(mac);
		WifiList filteredByMac = new WifiList(topTen);
		filteredByMac.filterMac(testMac);
		// Test and write KML:
		filteredByMac.printWifiList(FILE_NAME_FILTER_MAC+mac);
		KmlFile.createKmlFile(filteredByMac, outputDir+"\\"+FILE_NAME_FILTER_MAC+mac.replace(':', '-'));
				
		// ------- Filter out duplicate MACs ----------- 
		WifiList filteredMacDuplicates = new WifiList(topTen);
		filteredMacDuplicates.filterMacDuplicates();
		KmlFile.createKmlFile(filteredMacDuplicates, outputDir+"\\"+FILE_NAME_FILTER_MAC_DUPlICATES);
		filteredMacDuplicates.printWifiList(FILE_NAME_FILTER_MAC_DUPlICATES);
		}
		
	END tests	
	**/}
 		
	// ------------ Getters for JUnit to test private variables ------------ //
	public static String getInputDir() {
		return inputDir;
	}
	public static String outputDir() {
		return outputDir;
	}
	
}




