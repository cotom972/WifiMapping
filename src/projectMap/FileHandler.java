package projectMap;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;



public class FileHandler {
	
	public enum FileReader { READALL,JUSTDB,EXCEPTDB,CSVS,KMLS,WIGGLE,NOTDBNOTWIGGLE};
	//	----------------------------- Extension Filters -----------------------------:
	static FilenameFilter csvExtensionFilter = new FilenameFilter() {
		public boolean accept(File pathname,String name) {
			String lowercaseExt = name.toLowerCase();
			if(lowercaseExt.endsWith(".csv"))
				return true;
			else
				return false;
		}
	};
	static FilenameFilter justDBFileFilter = new FilenameFilter() {
		public boolean accept(File pathname,String name) {
			if(name.startsWith(Main.FILENAME_DB_STARTSWITH))
				return true;
			else
				return false;
		}
	};
	static FilenameFilter ExceptDBFileFilter = new FilenameFilter() {
		public boolean accept(File pathname,String name) {
			if(name.startsWith(Main.FILENAME_DB_STARTSWITH))
				return false;
			else if(name.endsWith(".csv")||name.endsWith(".kml"))
				return true;
			else
				return false;
				
		}
	};
	static FilenameFilter kmlExtensionFilter = new FilenameFilter() {
		public boolean accept(File pathname,String name) {
			String lowercaseExt = name.toLowerCase();
			if(lowercaseExt.endsWith(".kml"))
				return true;
			else
				return false;
		}
	};
	static FilenameFilter readAllJustFiles = new FilenameFilter() {
		public boolean accept(File pathname,String name) {
			if(pathname.isFile())
				return true;
			else
				return false;
		}
	};
	static FilenameFilter wiggleFileFilter = new FilenameFilter() {
		public boolean accept(File pathname,String name) {
			if(name.toLowerCase().startsWith(Main.FILENAME_WIGGLE_STARTSWITH.toLowerCase()))
				return true;
			else
				return false;
		}
	};
	static FilenameFilter notDBNotWiggleFileFilter = new FilenameFilter() {
		public boolean accept(File pathname,String name) {
			return (!wiggleFileFilter.accept(pathname, name)&&!justDBFileFilter.accept(pathname, name)&&name.endsWith(".csv"));
		}
	};
	
	//	-------------------------------- Functions ----------------------------------:
	
	
	/**
	 * Reads a specific file from directory.
	 * @param path - Path of file.
	 * @param ext - File's extension (for validation).
	 * @return - File
	 * @throws Exception - Throws exception in case file isn't in the extension requested.
	 */
	public static File readFile(String path, String ext) throws Exception {
		File file = new File(path);
		String fileExtension = FilenameUtils.getExtension(path);
		if(!fileExtension.equalsIgnoreCase(ext))
			throw new Exception("Not an CSV file");
		else {
			return file;
		}	
	}
	/**
	 * Reades files from a directory. Reades only files with extension requested.
	 * @param dir - Directory to read from.
	 * @param ext - Extension of files that are relevant.
	 * @return	File[] containing all files from dir.
	 */
	public static File[] readDirFiles(String dir, FileReader whatToRead ) {
		File[] files = null;
		switch(whatToRead) {
			case JUSTDB:
				files = new File(dir).listFiles(justDBFileFilter);
				break;
			case EXCEPTDB:
				files = new File(dir).listFiles(ExceptDBFileFilter);
				break;
			case CSVS:
				files = new File(dir).listFiles(csvExtensionFilter);
				break;
			case KMLS:
				files = new File(dir).listFiles(kmlExtensionFilter);
				break;
			case READALL:
				files = new File(dir).listFiles(readAllJustFiles);
				break;
			case WIGGLE:
				files = new File(dir).listFiles(wiggleFileFilter);
				break;
			case NOTDBNOTWIGGLE:
				files = new File(dir).listFiles(notDBNotWiggleFileFilter);
				break;
			default:
				files = new File(dir).listFiles(readAllJustFiles);
				break;
		}
		return files;
	}
}
