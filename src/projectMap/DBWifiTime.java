package projectMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.omg.CORBA.COMM_FAILURE;

/**
 * This class represents a list of DBWifiTimeStamps. 
 * @author Tom Cohen
 *
 */
public class DBWifiTime{

	private ArrayList<DBWifiTimeStamp> _DBList  = new ArrayList<DBWifiTimeStamp>();
	private ArrayList<Date> _listDates = new ArrayList<Date>();
	
	
	// ----------------------------------------- Constructors ------------------------------------------------:
	public DBWifiTime() {
		this._DBList = new ArrayList<DBWifiTimeStamp>();
		this._listDates = new ArrayList<Date>();
	}
	public ArrayList<Date> get_listDates() {
		return _listDates;
	}
	public void set_listDates(ArrayList<Date> _listDates) {
		this._listDates = _listDates;
	}
	/**
	 * Constructs a list of 'DBWifiTImeStamps' for each date that apears in the list.
	 * @param wifis Wifi list to convert from.
	 * @throws Exception
	 */	
	public DBWifiTime(WifiList wifis) throws Exception {
		this._listDates = wifis.get_listDates();
		for(Date date: this._listDates) {
			this._DBList.add(new DBWifiTimeStamp(date, wifis));
		}
	}
	/** 
	 * Constructs a DBWifiTime out of an array of files that must be in a DBWifiTime Format. (See 'Main.class' FINAL variables for detailed description).
	 * @param dbFiles must be an array holding files in a DBWifiTime format.
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public DBWifiTime(File[] dbFiles) throws NumberFormatException, Exception {
		this(CsvFile.readDBWifiTimeFileFormat(dbFiles)._DBList);
	}
	public DBWifiTime(ArrayList<DBWifiTimeStamp> array) {
		this._DBList = new ArrayList<DBWifiTimeStamp>(array);
		for(DBWifiTimeStamp timeStamp: array) {
			timeStamp.getWifis().generateDates();
			this._listDates.addAll(timeStamp.getWifis().get_listDates());
		}
	}

	// --------------------------------------- WifiTime functions --------------------------------------------:
	/**
	 * Merge current DBWifiTime with another. Sort rows by date at the end.
	 * @param other DBWifiTime to merge.
	 */
	public void mergeWifiTime(DBWifiTime other) {
		for(DBWifiTimeStamp stamp: other._DBList) {
			this._DBList.add(stamp);
		}
		this.sortByDates();
	}
	
	// ---------------------------------- Fetch Similar WifiTimeStamps ---------------------------------------:
	public DBWifiTime fetchSimilarWifiTimeStampsFromDB(DBWifiTimeStamp source, int maxSize) throws Exception {
		
		DBWifiTime result;
		ArrayList<DBWifiTimeStamp> resultList = new ArrayList<DBWifiTimeStamp>();
		// 1. Sort DB by similarity to 'source'. 2. Cut off first "Main.MAX_NUM_OF_ELEMENTS_FOR_SIMILARITY" rows.
		this.sortBySimilarityTo(source);
		for(int i=0; i<this.getWifiTimeStamps().size() && i<maxSize;i++) {
			if(Geo.similarityBetween(this.getWifiTimeStamps().get(i), source)!=Main.SIMILARITY_MIN_RANGE)
				resultList.add(new DBWifiTimeStamp(this.getWifiTimeStamps().get(i)));
		}
		result = new DBWifiTime(resultList);  
		result.sortBySimilarityTo(source);
		return result;
	}
	public DBWifiTime fetchSimilarWifiTimeStampsFromDB(DBWifiTimeStamp source) throws Exception {
		return this.fetchSimilarWifiTimeStampsFromDB(source,Geo.MAX_NUM_OF_ELEMENTS_FOR_AVERAGE);
	}
	/** 
	 * @overload 
	 * @param DB
	 * @throws Exception
	 */
	public void estimateMissingLocations(DBWifiTime DB) throws Exception {
		// Estimate location just for rows that don't have location.
		for(DBWifiTimeStamp WifiStamp: this._DBList) {
			if(!WifiStamp.hasLocation())			
				WifiStamp.setScanLocation(Geo.estimateWifiTimeStampLocations(DB, WifiStamp));
		}
	}
	
	// --------------------------------------------- Sorts ---------------------------------------------------:
	public void sortBySimilarityTo(DBWifiTimeStamp other) throws Exception {
		Collections.sort(this._DBList, new SimilarityComparator(other));
	}
	public void sortByDates() {
		Collections.sort(this.getWifiTimeStamps(), new DateComparator());
	}
	//	------------ Comparators ------------:
	/**
	 * Comparator for comparing 2 DBWifiTimeSTamps by their date.
	 * @author Tom Cohen
	 *
	 */
	static class DateComparator implements Comparator<DBWifiTimeStamp>{
		@Override
		public int compare(DBWifiTimeStamp o1, DBWifiTimeStamp o2) {
			return o1.getTimeStamp().compareTo(o2.getTimeStamp());
		}
	}
	/** 
	 * Comparator to sort by similarity.
	 */
	static class SimilarityComparator implements Comparator<DBWifiTimeStamp>{
		DBWifiTimeStamp _dbWifiTimeStamp;
		public SimilarityComparator(DBWifiTimeStamp wifiTimeStamp) throws Exception {
			this._dbWifiTimeStamp = wifiTimeStamp;
		}
		@Override
		public int compare(DBWifiTimeStamp o1, DBWifiTimeStamp o2) {
			if(Geo.similarityBetween(this._dbWifiTimeStamp, o1)>Geo.similarityBetween(this._dbWifiTimeStamp, o2))
				return -1;
			if(Geo.similarityBetween(this._dbWifiTimeStamp, o1)==Geo.similarityBetween(this._dbWifiTimeStamp, o2))
				return 0;
			else
				return 1;
		}
	}
	// -------------------------------------------- Getters --------------------------------------------------:
	public ArrayList<DBWifiTimeStamp> getWifiTimeStamps() {
		return this._DBList;
	}
	public void setWifiTimeStamps(ArrayList<DBWifiTimeStamp> newList) {
		this._DBList = newList;
	}
	
	// -------------------------------------------- Prints  --------------------------------------------------:
	public void printAll() {
		for(DBWifiTimeStamp timeStamp: this._DBList) {
			timeStamp.printDBWifiTimeStamp();	
		}
	}
	public void printSimilarityTo(DBWifiTimeStamp origin) {
		for(DBWifiTimeStamp timeStamp: this.getWifiTimeStamps()) {
			System.out.println(Geo.similarityBetween(timeStamp, origin));
		}
	}
	
	// --------------------------------------- Write To Files --------------------------------------------:
	/**
	 * Write current DBWifiTime to '.csv' file. 
	 * @param path Path to where the file will be written. You need to INCLUDE "\\" in path.
	 * @param filename Name of the file to be written.
	 */
	public void writeToCsv(String path, String filename) {
		CsvFile.writeToDBWifiTimeCSV(path+filename, this);
	}
}












//
//for(int i=0; i<csvFiles.length;i++) {				// go over all files
//	if(csvFiles[i]!=null && (csvFiles[i].getName().startsWith(Main.FILENAME_DB_STARTSWITH)|| !isDB ||csvFiles[i].getName().contains("_BM"))) { // check it they begin with DB filename convention.
//		Scanner inputStream = new Scanner(csvFiles[i]);
//		String query = inputStream.nextLine();
//		String[] data = query.split(CsvFile.COMMA_DELIMITER);
//		// Check if file has header, if so skip, else build first DBWifiStamp in list and continue the rest in the while loop.
//		while(CsvFile.csvHeader(data)!=CsvFile.HEADER.NOHEADER) {
//			query = inputStream.nextLine();
//			data = query.split(CsvFile.COMMA_DELIMITER);
//		}	
//		this._DBList.add(new DBWifiTimeStamp(data));
//		// Build rest of DBWifiStampList
//		while(inputStream.hasNextLine())	{			// convert each row of each file to DBWifiTimeStamp object, and add to total list.
//			query = inputStream.nextLine();
//			data = query.split(CsvFile.COMMA_DELIMITER);
//			if(!query.isEmpty()) {
//				this._DBList.add(new DBWifiTimeStamp(data));
//			}
//		}
//		inputStream.close();
//	}
//	
//}
//this.sortByDates();



