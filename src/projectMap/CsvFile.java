package projectMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CsvFile extends FileHandler {
	
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPERATOR = "\n";
	private static final String FILE_HEADER = "MAC,SSID,AuthMode,FirstSeen,Channel,RSSI,CurrentLatitude,CurrentLongitude,AltitudeMeters,AccuracyMeters,Type";

	/**
	 * Writes a '.csv' file out of an Arraylist<Wifi>
	 * @param filename - Name of file to be given.
	 * @param WifiList - List of wifis to write to c
	 */
	public static void writeCsvFile(String filename, WifiList WifiList) {
		FileWriter fileWriter = null;
		try {
           fileWriter = new FileWriter(filename);

			// Header:
	        fileWriter.append(FILE_HEADER.toString());
	        fileWriter.append(NEW_LINE_SEPERATOR);
	        
	        // Write Wifis to file:
	        for(Wifi wifi: WifiList.WifiList) {
	        	fileWriter.append(String.valueOf(wifi.getMac().getAddress()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getSsid()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getAuthMode()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getFirstSeen()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getChannel()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getRssi()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getLat()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getLon()));
	        	fileWriter.append(COMMA_DELIMITER);
	        	fileWriter.append(String.valueOf(wifi.getAlt()));
	        	fileWriter.append(COMMA_DELIMITER);
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
	
	/**
	 * Transform a CSV file to a an ArrayList<Wifi>.	
	 * @param f - File object, of "csv" format.
	 * @return - ArrayList<Wifi>.
	 * @throws Exception - Throws an exception when an error accurs in file reading process.
	 */
	public static WifiList csvToWifiList(File f) throws Exception{
		
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
		return new WifiList(wifiList);
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
}
