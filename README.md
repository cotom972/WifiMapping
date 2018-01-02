# WifiMapping

WifiMapping is used to proccess data exported from the [Wiggle](https://play.google.com/store/apps/details?id=net.wigle.wigleandroid&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1) App, and proccess it to a Google Earth KML file.

## Installation
Only configurations need to be made in order to use WifiMapping on your local machine is found at the 'Main.class' at the 'inputDir' and 'outputDir'. Make sure you are inputing files exported from WiggleWifi App as WifiMapping works only with this format.

Other then that make sure you install the external Jars found in the "\ExternalFiles\Jars" dir.

```
// ----------------------- Paths configuration -----------------------//
private static String inputDir = "C:\\WifiMapping\\ExternalFiles\\Input-Files";
private static String outputDir =  "C:\\WifiMapping\\ExternalFiles\\Resulted-Files";
```

## Usage and Class structure

### Wifi
#### Wifi.class
Represents each network found on the exported WiggleWifi App file as a Wifi java obj containing all of its relevant data.

#### WifiList.class
Respresent a list of wifis, stored in an ArrayList<Wifi> and can operate different manipulations/filter functions.

### File Handlers
#### FileHandler.class
Basic class just for handling read/write of files.

#### CsvFile.class
Extends FileHandler.class and functions as a "layer" above it to handle .csv files demands.

#### KmlFile.class
Extends FileHandler.class and functions as a "layer" above it to handle .kml files demands.



## DataBase usage
Database files are generated from a 'DBWifiTime' object and can be written to a file using CsvFile.class, or via the DBWifiTime object itself (calls CsvFile methods).

### DBWifiTime
DBWifiTime is the object holding a DB table where each row is of a 'DBWifiTimeStamp' type.

### DBWifiTimeStamp
Represents a row in the database. Each DBWifiTimeStamp represent 1 second of WifiScan from the WiggleWifi application, and holds a number of wifi points that were scanned at this time.
Number of Wifi point per each DBWifiTimeStamp is set at the 'Main.class' final variables, as well as other configurations:

```
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
 ```