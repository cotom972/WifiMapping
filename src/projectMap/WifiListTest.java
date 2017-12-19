package projectMap;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Test;

public class WifiListTest {
	
	private static final String inputTestDir = "C:\\WifiMapping\\ExternalFiles\\JUnit - Input test samples";


	@Test
	public void testWifiListWifiList() throws Exception {
		File[] filesCsv = FileHandler.readDirFiles(inputTestDir ,".csv");
		WifiList wifisFromWifiList = new WifiList(CsvFile.csvToWifiList(filesCsv[0]));
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
		File[] filesCsv = FileHandler.readDirFiles(inputTestDir ,".csv");
		WifiList wifis = new WifiList(CsvFile.csvToWifiList(filesCsv[0]));
		wifis.sortBySignal();
		
		// Check if list is sorted by signal in descending order.
		double tmpSignal = wifis.getArrayList().get(0).getRssi();
		for(Wifi wifi: wifis.getArrayList().subList(1, wifis.getSize()-1)) {
			assertTrue(wifi.getRssi()<=tmpSignal);
			tmpSignal = wifi.getRssi();
		}
		
	}

}
