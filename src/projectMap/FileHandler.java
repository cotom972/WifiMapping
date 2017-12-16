package projectMap;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FilenameUtils;



public class FileHandler {
	
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
	 * @return	File[]
	 */
	public static File[] readDirFiles(String dir, String ext) {
		
		// Create filter for extensions that are relevant:
		FilenameFilter extFilter = new FilenameFilter() {
			public boolean accept(File dir,String ext) {
				String lowercaseExt = ext.toLowerCase();
				
				if(lowercaseExt.endsWith(ext))
					return true;
				else
					return false;
			}
		};
		
		// Read files: 
		File[] files = new File(dir).listFiles(extFilter);
		return files;
	}
	
}
