package projectMap;

import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.vecmath.Point3d;
import org.junit.Assert;
import org.junit.Test;

public class WifiTest {
	
	// Valid Wifi.obj parameters for testing:
	private String mac = "7c:b7:33:ea:a4:2e";
	private String ssid = "BezeqFree";
	private String authMode = "[WPA2-PSK-CCMP][ESS]";
	private String firstSeen = "13/11/2017 17:42";
	private double channel = 2;
	private double rssi = -60;
	private double accuracy = 6;
	private String type = "WIFI";
	private double lon = 34.77270189;
	private double lat = 32.06148439;
	private double alt = 46;


	@Test
	public void testWifiWifi() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		Date testDate = new SimpleDateFormat(Wifi.DATE_FORMAT).parse(firstSeen);
	    assertEquals("Mac was not passed correctly to Wifi Object",mac, wifi.getMac().getAddress());
	    assertEquals("SSID was not passed correctly to Wifi Object",ssid, wifi.getSsid());
	    assertEquals("authMode was not passed correctly to Wifi Object",authMode, wifi.getAuthMode());
	    assertEquals("firstSeen was not passed correctly to Wifi Object",testDate, wifi.getDate());
	    assertEquals("channel was not passed correctly to Wifi Object",channel, wifi.getChannel(),0);
	    assertEquals("rssi was not passed correctly to Wifi Object",rssi, wifi.getRssi(),0);
	    assertEquals("lat was not passed correctly to Wifi Object",lat, wifi.getPoint3d().y,0);
	    assertEquals("lon was not passed correctly to Wifi Object",lon, wifi.getPoint3d().x,0);
	    assertEquals("alt was not passed correctly to Wifi Object",alt, wifi.getPoint3d().z,0);
	    assertEquals("accuracy was not passed correctly to Wifi Object",accuracy, wifi.getAccuracy(),0);
	    assertEquals("type was not passed correctly to Wifi Object",type, wifi.getType());
	}

	@Test
	public void testGetAccuracy() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		assertEquals(accuracy, wifi.getAccuracy(),0);
	}

	@Test
	public void testGetMac() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		assertEquals(mac, wifi.getMac().getAddress());
	}

	@Test
	public void testGetSsid() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		assertEquals(ssid, wifi.getSsid());
	}

	@Test
	public void testGetRssi() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		assertEquals(rssi, wifi.getRssi(),0);
	}

	@Test
	public void testGetChannel() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		assertEquals(channel, wifi.getChannel(),0);
	}

	@Test
	public void testGetLat() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		assertEquals(lat, wifi.getPoint3d().y,0);
	}

	@Test
	public void testGetLon() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		assertEquals(lon, wifi.getPoint3d().x,0);
	}

	@Test
	public void testGetAlt() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		assertEquals(alt, wifi.getPoint3d().z,0);
	}

	@Test
	public void testGetType() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		assertEquals(type, wifi.getType());
	}

	@Test
	public void testGetPoint3d() throws Exception {
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		Point3d point = new Point3d(lon,lat,alt);
	    Assert.assertNotNull(wifi.getPoint3d());  //Checks for null
	    assertTrue(point.x == wifi.getPoint3d().x && point.y==wifi.getPoint3d().y && point.z == wifi.getPoint3d().z);	    
	}

	@Test
	public void testGetDate() throws Exception {
		Date date = Wifi.SIMPLE_DATE_FORMAT.parse(firstSeen);
		Wifi wifi = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
	    Assert.assertNotNull(wifi.getPoint3d());  //Checks for null
	    assertTrue(date.compareTo(wifi.getDate())==0);
	}
	
	@Test
	public void testEqualTo() throws Exception {
		Wifi o1 = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		Wifi o2 = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		
		Wifi omac = new Wifi("different", ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		Wifi ossid = new Wifi(mac, "different", authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		Wifi oauthMode = new Wifi(mac, ssid, "different", firstSeen, channel, rssi, lat, lon, alt, accuracy, type);
		Wifi ofirstSeen = new Wifi(mac, ssid, authMode, "13/11/2017 17:43", channel, rssi, lat, lon, alt, accuracy, type);
		Wifi ochannel = new Wifi(mac, ssid, authMode, firstSeen, 3, rssi, lat, lon, alt, accuracy, type);
		Wifi orssi = new Wifi(mac, ssid, authMode, firstSeen, channel, 100, lat, lon, alt, accuracy, type);
		Wifi oaccuracy = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, 100, type);
		Wifi otype = new Wifi(mac, ssid, authMode, firstSeen, channel, rssi, lat, lon, alt, accuracy, "different");
		assertTrue(o1.equalTo(o2));
		assertFalse(o1.equalTo(omac));
		assertFalse(o1.equalTo(ossid));
		assertFalse(o1.equalTo(oauthMode));
		assertFalse(o1.equalTo(ofirstSeen));
		assertFalse(o1.equalTo(ochannel));
		assertFalse(o1.equalTo(orssi));
		assertFalse(o1.equalTo(oaccuracy));
		assertFalse(o1.equalTo(otype));

	}
}
