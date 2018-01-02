package projectMap;

public class WiggleScanner {
	private String _WigleWifi;
	private String _appRelease;
	private String _model;
	private String _release;
	private String _device;
	private String _display;
	private String _board;
	private String _brand;
	
	
	
	
	public WiggleScanner() {
		this._WigleWifi = "N/A";
		this._appRelease = "N/A";
		this._model = "N/A";
		this._release = "N/A";
		this._device = "N/A";
		this._display = "N/A";
		this._board = "N/A";
		this._brand = "N/A";
	}
	public WiggleScanner(WiggleScanner other) throws Exception {
		if(other == null) {
			this._WigleWifi = "N/A";
			this._appRelease = "N/A";
			this._model = "N/A";
			this._release = "N/A";
			this._device = "N/A";
			this._display = "N/A";
			this._board = "N/A";
			this._brand = "N/A";
			
		}
		else {
			this._WigleWifi = other._WigleWifi;
			this._appRelease = other._appRelease;
			this._model = other._model;
			this._release = other._release;
			this._device = other._device;
			this._display = other._display;
			this._board = other._board;
			this._brand = other._brand;
		}
	}
	public WiggleScanner(String[] data) throws Exception {
		if(data == null) {
			this._WigleWifi = "N/A";
			this._appRelease = "N/A";
			this._model = "N/A";
			this._release = "N/A";
			this._device = "N/A";
			this._display = "N/A";
			this._board = "N/A";
			this._brand = "N/A";
			
		}
		else if(data[1].contains(Main.WIGGLEWIFI_HEADER_FORMAT[2])) {
			this._WigleWifi = "N/A";
			this._appRelease = "N/A";
			this._model = data[2];
			this._release = "N/A";
			this._device = data[2];
			this._display = "N/A";
			this._board = "N/A";
			this._brand = "N/A";
		}
		else {
			this._WigleWifi = data[0];
			this._appRelease = data[1];
			this._model = data[2];
			this._release = data[3];
			this._device = data[4];
			this._display = data[5];
			this._board = data[6];
			this._brand = data[7];
		}
	}
	
	// --------------------------------------------- Getters -------------------------------------------------:
	public String getWiggleWifi() {
		return this._WigleWifi;
	}
	public String getAppRelease() {
		return this._appRelease;
	}
	public void setAppRelease(String appRelease) {
		this._appRelease = appRelease;
	}
	public String getModel() {
		return this._model;
	}
	public void setModel(String model) {
		this._model	= model;
	}
	public String getRelease() {
		return this._release;
	}
	public String getDevice() {
		return this._device;
	}
	public void setDevice(String device) {
		this._device = device;
	}
	public String getDisplay() {
		return this._display;
	}
	public String getBoard() {
		return this._board;
	}
	public String getBrand() {
		return this._brand;
	}
	
}
