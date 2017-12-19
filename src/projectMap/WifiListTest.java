package projectMap;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class WifiListTest {
	
	

	@Test
	public void testWifiListArrayListOfWifi() {
		fail("Not yet implemented");
	}

	@Test
	public void testWifiListWifiList() throws Exception {
		File[] filesCsv = FileHandler.readDirFiles(Main.getInputDir() ,".csv");
		
		WifiList wifisFromWifiList = new WifiList(CsvFile.csvToWifiList(filesCsv[0]));
		WifiList wifisFromArrayList = new WifiList(CsvFile.csvToWifiArrayList(filesCsv[0]));
		

	}

	@Test
	public void testSortBySignal() {
		fail("Not yet implemented");
	}

}
