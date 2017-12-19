package projectMap;

import java.io.File;
import java.io.IOException;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Document;

public class KmlFile extends FileHandler {
	
	/**
	 * Writes a '.kml' file from a given wifi ArrayList<Wifi>.
	 * @param wifis - List of wifis to write.
	 * @param fileName - Name of the file to be writen.
	 * @throws IOException - Throw exception in case error accurs in writing file.
	 */
	public static void createKmlFile(WifiList wifis, String fileName) throws IOException {
		Kml kml = new Kml();
		Document document = kml.createAndSetDocument().withName("Wifi's");
		for(Wifi wifi: wifis.getArrayList()) {
		document.createAndAddPlacemark().withName("("+wifi.getMac().getAddress()+") "+wifi.getSsid()).withOpen(true).createAndSetPoint().addToCoordinates(wifi.getLon(), wifi.getLat(), wifi.getAlt());
		}
		kml.setFeature(document);
		kml.marshal(new File(fileName+".kml"));

	}
}
	
