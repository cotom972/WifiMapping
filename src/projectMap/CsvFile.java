package projectMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CsvFile extends FileHandler {
	
	public enum HEADER{WIGGLE,DBWIFITIME,SINGLE_LIST_HEADER,NOHEADER};
	
	static final String COMMA_DELIMITER = ",";
	static final String NEW_LINE_SEPERATOR = "\n";
	static final String HEADER_COMB_DB_COLUMNS = "Time,ID,Lat,Lon,Alt,#WiFi networks"; 
	static final String[] HEADER_COMB_DB_WIFI_COLUMNS = {"SSID","MAC","Frequncy","Signal"};
	static final int NUM_OF_WIFIS_ON_DB_ROW = 10;


	public static String DBWifiTimeStampListHeader() {
		String result = HEADER_COMB_DB_COLUMNS;
		for(int i=0; i<DBWifiTimeStamp.NUM_OF_WIFIS_PER_TIMESTAMP; i++) {
			for(int j=0; j<HEADER_COMB_DB_WIFI_COLUMNS.length;j++) {
				result+=COMMA_DELIMITER+HEADER_COMB_DB_WIFI_COLUMNS[j]+(i+1);
			}
		}
		return result;
	}

	// ---------------------------------- Read From CSV  ---------------------------------- //
	
	// CSV --> DBWifiTimeStamp
	/**
	 * Convert a specific row from a DBWifiTime csv file, to a DBWifiTimeStamp Object.
	 * @param f File to read row from.
	 * @param rowNumber Which row to read
	 * @param scanner If there is already a scanner open (in case called from another method) use the same one.
	 * Else, build a new Scanner.
	 * @return A DBWifiTimeStamp object holding data from the requested row.
	 * @throws Exception
	 */
	public static DBWifiTimeStamp readDBSingleWifiTimeStampFromCsv(File f, int rowNumber, Scanner scanner) throws Exception {
		if(!f.getName().startsWith(Main.FILENAME_DB_STARTSWITH))
			throw new Exception("No DBWifiTimeStamp files were found. Files must begin with DBWifiTimeStamp.class final variable 'BEGINNING_OF_DB_FILENAME'."
								+"\nFile:"+f.getPath());
		DBWifiTimeStamp result;
		try {
			Scanner inputStream = scanner == null ? new Scanner(f): scanner;
			for(int i=0; i<rowNumber;i++)			// Go to requested row.
				inputStream.nextLine();
			String query = inputStream.nextLine();
			String[] data = query.split(COMMA_DELIMITER);
			
			result = new DBWifiTimeStamp(data);
			if(scanner==null) {
				inputStream.close();
			}
		}
		catch(Exception e) {
			throw new Exception("Error in reading csv to DBWifiStamp. Check if csv is in format of DBWifiTimeStamp");
		}
		return result;
		
	}
	public static DBWifiTimeStamp readDBSingleWifiTimeStampFromCsv(File f, int rowNumber) throws Exception {
		return readDBSingleWifiTimeStampFromCsv(f,rowNumber,null);
	}
	public static DBWifiTime readWiggleToDBWifiTime(File [] files) throws Exception {
		
		WifiList resultWifiList = readWiggleToWifiList(files);
		DBWifiTime result = new DBWifiTime(resultWifiList);
		return result;
	}

	// CSV --> DBWifiTime
	/**
	 * Reads a .csv DBWifiTime file format and converts it to a DBWifiTime obj.
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static DBWifiTime readDBWifiTimeFileFormat(File f) throws Exception {
		
		DBWifiTime result = new DBWifiTime();		
		Scanner inputStream = new Scanner(f);
		while(csvHeader(inputStream.nextLine().split(COMMA_DELIMITER))!=HEADER.NOHEADER) {
		}
		while(inputStream.hasNext()) {
			result.getWifiTimeStamps().add(new DBWifiTimeStamp(inputStream.nextLine().split(COMMA_DELIMITER)));
		}
		inputStream.close();
		result.sortByDates();
		return result;
	}
	/**
	 * 
	 * Read a DBWifiTime formated CSV files into one DBWifiTime.
	 * @param dbFiles Array of DBWifiTIme formated files.
	 * @return One DBWifiTime object containing all data from all files merged together.
	 * @throws Exception
	 */
	public static DBWifiTime readDBWifiTimeFileFormat(File[] dbFiles) throws Exception {
		
		DBWifiTime result = new DBWifiTime();
		for(int i=0; i<dbFiles.length;i++) {
			result.mergeWifiTime(readDBWifiTimeFileFormat(dbFiles[i]));
		}
		result.sortByDates();
		return result;
	}
	
	// CSV --> WifiList
	/**
	 * Transform a CSV file to a an ArrayList<Wifi>.	
	 * @param f - File object, of "csv" format.
	 * @return - ArrayList<Wifi>.
	 * @throws Exception - Throws an exception when an error accurs in file reading process.
	 */
	public static WifiList readWiggleToWifiList(File f) throws Exception{
		
		WifiList result = new WifiList();
		ArrayList<Wifi> wifiList = new ArrayList<Wifi>();
		try {
			Scanner inputStream = new Scanner(f);
			result.set_wiggleScanner(new WiggleScanner(inputStream.nextLine().split(COMMA_DELIMITER)));
			inputStream.nextLine(); // Skip columns row.
			while(inputStream.hasNext()) {
				result.addWifi(new Wifi(inputStream.nextLine().split(COMMA_DELIMITER)));
			}
			inputStream.close();
		}
		finally {
		}
		return result;
	}	
	/**
	 * Merges all data from all files, and constructs ONE WifiList from it.
	 * @param files Array of .csv files contaning data to convert to WifiList.
	 * @return WifiList object containing data merged from all of the files.
	 * @throws Exception
	 */
	public static WifiList readWiggleToWifiList(File[] files) throws Exception {
		
		WifiList result = new WifiList();
		for(int i=0; i<files.length;i++) {
			WifiList tmp = CsvFile.readWiggleToWifiList(files[i]);
			result.mergeWithList(tmp);
		}
		result.generateDates();
		return result;
	}

	
	// ---------------------------------- Write From CSV  ---------------------------------- //
	
	// WifiList --> CSV
	/**
	 * Writes a WifiList to a .csv file.
	 * @param filename - Name of file to be given.
	 * @param WifiList - List of wifis to write to csv.
	 */
	public static void writeSingleWifiListToCsv(String filename, WifiList WifiList) {
		FileWriter fileWriter = null;
		try {
           fileWriter = new FileWriter(filename);

			// Header:
	        fileWriter.append(Main.HEADER_SINGLE_LIST.toString());
	        fileWriter.append(NEW_LINE_SEPERATOR);
	        
	        // Write Wifis to file:
	        for(Wifi wifi: WifiList.getArrayList()) {
	        	fileWriter.append(String.valueOf(wifi.getMac().getAddress()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getSsid()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getAuthMode()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getFirstSeen()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	
	        	if(wifi.getChannel()==Main.NO_INPUT_INT)
		        	fileWriter.append(String.valueOf("N/A"));
	        	else
	        		fileWriter.append(String.valueOf(wifi.getChannel()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	
	        	if(wifi.getSignal().getStrength()==Main.NO_INPUT_INT)
		        	fileWriter.append(String.valueOf("N/A"));
	        	else
	        		fileWriter.append(String.valueOf(wifi.getSignal().getStrength()));
	        	fileWriter.append(COMMA_DELIMITER);
	        
	        	if(wifi.getLat()==Main.NO_INPUT_INT)
		        	fileWriter.append(String.valueOf(Main.NO_INPUT_STRING));
	        	else
	        		fileWriter.append(String.valueOf(wifi.getLat()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	if(wifi.getLon()==Main.NO_INPUT_INT)
		        	fileWriter.append(String.valueOf(Main.NO_INPUT_STRING));
	        	else
	        		fileWriter.append(String.valueOf(wifi.getLon()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	
	        	if(wifi.getAlt()==Main.NO_INPUT_INT)
		        	fileWriter.append(String.valueOf(Main.NO_INPUT_STRING));
	        	else
	        		fileWriter.append(String.valueOf(wifi.getAlt()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	
	        	if(wifi.getAccuracy()==Main.NO_INPUT_INT)
		        	fileWriter.append(String.valueOf(Main.NO_INPUT_STRING));
	        	else
	        		fileWriter.append(String.valueOf(wifi.getAccuracy()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	
	        	fileWriter.append(String.valueOf(wifi.getType()));
	        	fileWriter.append(NEW_LINE_SEPERATOR);        
	        	}
			}
		catch(Exception e){
           System.out.println("Error in writing CSV file");
           e.printStackTrace();
		}
		finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			}
			catch(IOException e) {
				 System.out.println("Error while flushing/closing fileWriter");
				 e.printStackTrace();
			}
		}
	}
	
	// DBWifiTime --> CSV
	public static void writeToDBWifiTimeCSV(String filename, DBWifiTime list) {
		if(!filename.endsWith(".csv"))
			filename+=".csv";
		String header = DBWifiTimeStampListHeader();
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filename);
			// Header:
	        fileWriter.append(header);
	        fileWriter.append(NEW_LINE_SEPERATOR);
	        for(DBWifiTimeStamp row: list.getWifiTimeStamps()) { 
	        	if(row.getWifis().getArrayList()!=null && row.getWifis().getArrayList().size()>0) {
		        	fileWriter.append(row.getTimeStamp());
		        	fileWriter.append(COMMA_DELIMITER);
		        	fileWriter.append(row.getWifis().getArrayList().get(0).getScanDetails().getModel()+row.getWifis().getArrayList().get(0).getScanDetails().getDevice());
		        	fileWriter.append(COMMA_DELIMITER);
		        	fileWriter.append(row.getScanLocation().y==Main.NO_INPUT_INT? Main.NO_INPUT_STRING: String.valueOf(row.getScanLocation().y));
		        	fileWriter.append(COMMA_DELIMITER);
		        	fileWriter.append(row.getScanLocation().x==Main.NO_INPUT_INT? Main.NO_INPUT_STRING: String.valueOf(row.getScanLocation().x));
		        	fileWriter.append(COMMA_DELIMITER);
		        	fileWriter.append(row.getScanLocation().z==Main.NO_INPUT_INT? Main.NO_INPUT_STRING: String.valueOf(row.getScanLocation().z));
		        	fileWriter.append(COMMA_DELIMITER);
		        	// #WifiNetworks
		        	fileWriter.append(Main.NO_INPUT_STRING);
		        	fileWriter.append(COMMA_DELIMITER);

			        // Write Wifis to file:
			        for(Wifi wifi: row.getWifis().getArrayList()) {
			        	fileWriter.append(String.valueOf(wifi.getSsid()));
			        	fileWriter.append(COMMA_DELIMITER);
			        	fileWriter.append(String.valueOf(wifi.getMac().getAddress()));
			        	fileWriter.append(COMMA_DELIMITER);
			        	if(wifi.getChannel()==Main.NO_INPUT_INT) {
				        	fileWriter.append(String.valueOf("N/A"));
				        	fileWriter.append(COMMA_DELIMITER);
			        	}
				        else {
			        		fileWriter.append(String.valueOf(wifi.getChannel()));
			        		fileWriter.append(COMMA_DELIMITER);
				        }
			        	if(wifi.getSignal().getStrength()==Main.NO_INPUT_INT) {
				        	fileWriter.append(String.valueOf("N/A"));
			        	fileWriter.append(COMMA_DELIMITER);
			        	}
			        	else {
			        		fileWriter.append(String.valueOf(wifi.getSignal().getStrength()));
				        	fileWriter.append(COMMA_DELIMITER);
		        		}
		        	}
			        fileWriter.append(NEW_LINE_SEPERATOR);
	        	}
	        	else {
		        	fileWriter.append(row.getTimeStamp());
		        	fileWriter.append(COMMA_DELIMITER);
		        	fileWriter.append(NEW_LINE_SEPERATOR);
	        	}
	        }
		}
		catch(Exception e){
	           System.out.println("Error in writing CSV file");
	           e.printStackTrace();
			}
			finally {
				try {
					fileWriter.flush();
					fileWriter.close();
				}
				catch(IOException e) {
					 System.out.println("Error while flushing/closing fileWriter");
					 e.printStackTrace();
				}
			}
	}

	
	
	// ------------------------------------------------------------------------------------- //

	

	
	/**
	 * Reads a .csv file and converts it into an ArrayList containing wifis.
	 * @param f File to read.
	 * @return an ArrayList
	 * @throws Exception Error in reading file.
	 */
	

	
	// TODO:
	public static WifiList readDBCsvToWifiList(File[] files) {
		WifiList result = new WifiList();
		
		return result;
	}
	

	
	
	public static ArrayList<Wifi> csvToWifiArrayList(File f) throws Exception{
			
			ArrayList<Wifi> wifiList = new ArrayList<Wifi>();
			
			try {
				Scanner inputStream = new Scanner(f);
				inputStream.nextLine();	// Skip first line
				inputStream.nextLine();	// Skip first line

				while(inputStream.hasNext()) {
					String query = inputStream.nextLine();
					String[] data = query.split(COMMA_DELIMITER);
					wifiList.add(new Wifi(data));
				}
				inputStream.close();
			}
			finally {
			}
			return wifiList;
	}
	
	public static DBWifiTime readCsvToDB(File[] files) throws Exception {
		
		DBWifiTime result = null;
		ArrayList<DBWifiTimeStamp> resultArray = new ArrayList<DBWifiTimeStamp>();
		
		if(files == null) {
			throw new Exception("Error: Files array is null.");
		}
		for(int i=0; i<files.length;i++) {
			try {
				Scanner inputStream = new Scanner(files[i]);
				String query = inputStream.nextLine();
				String[] data = query.split(COMMA_DELIMITER);
				while(csvHeader(data)!=HEADER.NOHEADER) {	// Check if there a header to skip
					 query = inputStream.nextLine();
					 data = query.split(COMMA_DELIMITER);
				}
				resultArray.add(new DBWifiTimeStamp(data));
				while(inputStream.hasNext()) {
					query = inputStream.nextLine();
					data = query.split(COMMA_DELIMITER);
					resultArray.add(new DBWifiTimeStamp(data));
				}
				inputStream.close();
			}
			finally {
				result = new DBWifiTime(resultArray);
			}			
		}
		return result;
	}
	
	public static HEADER csvHeader(String[] query) {
		
		HEADER result = HEADER.NOHEADER;
		
		for(int i=0; i<query.length && i<Main.WIGGLEWIFI_HEADER_FORMAT.length;i++) {
			if(!query[i].contains(Main.WIGGLEWIFI_HEADER_FORMAT[i]))
					break;
			if(i==Main.WIGGLEWIFI_HEADER_FORMAT.length-1)
				return HEADER.WIGGLE;
		}
		for(int i=0; i<query.length && i<Main.DBWIFITIME_HEADER_FORMAT.length;i++) {
			if(!query[i].contains(Main.DBWIFITIME_HEADER_FORMAT[i]))
				break;
			if(i==query.length-1)
				return HEADER.DBWIFITIME;
		}
		for(int i=0; i<query.length && i<Main.HEADER_SINGLE_LIST.length;i++) {
			if(!query[i].contains(Main.HEADER_SINGLE_LIST[i]))
				break;
			if(i==query.length-1)
				return HEADER.SINGLE_LIST_HEADER;
		}
		return result;
		}
		
	}
		
	
	

//
//// Check headers
//switch(csvHeader(data)) {
//case DBWIFITIME:
//	query = inputStream.nextLine();
//	data = query.split(COMMA_DELIMITER);
//	wiggleScan = new WiggleScanner(data);
//	break;
//case WIGGLE:
//	wiggleScan = new WiggleScanner(data);
//	inputStream.nextLine();
//	query = inputStream.nextLine();
//	data = query.split(COMMA_DELIMITER);
//	break;
//case SINGLE_LIST_HEADER:
//	
//	
//
//}
//
//wifiList.add(new Wifi(data,wiggleScan));
//while(inputStream.hasNext()) {
//	query = inputStream.nextLine();
//	data = query.split(COMMA_DELIMITER,-1);
//	wifiList.add(new Wifi(data, wiggleScan));
//}
//inputStream.close();
