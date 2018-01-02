package projectMap;

import static org.junit.Assert.*;
import java.io.File;
import java.util.Date;

import org.junit.Test;

import projectMap.FileHandler.FileReader;

public class WifiListTest {
	
	private static final String inputTestDir = "C:\\WifiMapping\\ExternalFiles\\JunitInput";
	private static final String inputBigCsv = "C:\\\\WifiMapping\\\\ExternalFiles\\\\JunitInput\\Big.csv";

	@Test
	public void testWifiListWifiList() throws Exception {
		File[] filesCsv = FileHandler.readDirFiles(inputTestDir ,FileReader.CSVS);
		WifiList wifisFromWifiList = new WifiList(CsvFile.readWiggleToWifiList(filesCsv[0]));
		WifiList wifisFromArrayList = new WifiList(CsvFile.csvToWifiArrayList(filesCsv[0]));
		
		// check if WifiList null:
		assertNotNull(wifisFromWifiList);
		assertNotNull(wifisFromArrayList);
		
		// check if ArrayList<Wifi> not null in both constructed WifiLists.
		assertNotNull(wifisFromWifiList.getArrayList());
		assertNotNull(wifisFromWifiList.getArrayList());
		
		// Check if both constructed WifiList from same file, has same size value:
		assertEquals(wifisFromWifiList.getSize(),wifisFromArrayList.getSize());
		
		// Check if both WifiList are holding the same Wifi objects. (as in values and not obj address).
		for(int i=0; i<wifisFromArrayList.getSize();i++) {
			assertTrue(wifisFromArrayList.getArrayList().get(i).equalTo(wifisFromWifiList.getArrayList().get(i)));
		}
	}
	@Test
	public void testSortBySignal() throws Exception {
		File[] filesCsv = FileHandler.readDirFiles(inputTestDir ,FileReader.CSVS);
		WifiList wifis = new WifiList(CsvFile.readWiggleToWifiList(filesCsv[0]));
		wifis.sortBySignal();
		
		// Check if list is sorted by signal in descending order.
		double tmpSignal = wifis.getArrayList().get(0).getSignal().getStrength();
		for(Wifi wifi: wifis.getArrayList().subList(1, wifis.getSize())) {
			assertTrue(wifi.getSignal().getStrength()<=tmpSignal);
			tmpSignal = wifi.getSignal().getStrength();
		}
		
	}
	@Test
	public void testRemoveAllMacs() throws Exception{
		File[] filesCsv = FileHandler.readDirFiles(inputTestDir ,FileReader.CSVS);
		WifiList wifis = new WifiList(CsvFile.readWiggleToWifiList(filesCsv[0]));
		Mac mac = new Mac("c0:ac:54:f8:87:b6");
		
		assertTrue(wifis.getNumOfMac(mac)>0);
		wifis.removeAllMacs(mac);
		assertTrue(wifis.getNumOfMac(mac)==0);
		
	}
	@Test
	public void testSortByDate() throws Exception{
		File[] filesCsv = FileHandler.readDirFiles(inputBigCsv ,FileReader.CSVS);
		WifiList wifis = new WifiList(CsvFile.readWiggleToWifiList(filesCsv[0]));
		Date previous = wifis.getArrayList().get(0).getDate();
		for(Wifi wifi: wifis.getArrayList().subList(1, wifis.getArrayList().size())) {
			assertTrue(wifi.getDate().compareTo(previous)>=1);
			previous = wifi.getDate();
		}
	}
}
