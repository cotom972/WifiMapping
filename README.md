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