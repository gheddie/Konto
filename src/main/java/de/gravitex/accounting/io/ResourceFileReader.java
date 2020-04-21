package de.gravitex.accounting.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import de.gravitex.accounting.provider.AccoutingDataProvider;

public class ResourceFileReader {

	public static File[] getResourceFiles(String mainFolder, String subFolder) {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = null;
		if (mainFolder == null) {
			url = loader.getResource(subFolder);	
		} else {
			url = loader.getResource(mainFolder + "/" + subFolder);
		}
		String path = url.getPath();
		File[] result = new File(path).listFiles();
		return result;
	}

	public static File getResourceFile(String folder, String fileName) {
		
		ClassLoader classLoader = ResourceFileReader.class.getClassLoader();
		String path = null;
		if (folder == null) {
			path = fileName;	
		} else {
			path = folder + "/" + fileName;
		}
		File file = new File(classLoader.getResource(path).getFile());
		return file;
	}
	
	public static Properties getProperties(String folderName, String fileName) throws IOException {
		
		Properties prop = new Properties();
		if (folderName != null) {
			prop.load(AccoutingDataProvider.class.getClassLoader().getResourceAsStream(folderName + "/" + fileName));
		} else {
			prop.load(AccoutingDataProvider.class.getClassLoader().getResourceAsStream(fileName));			
		}
		return prop;
	}
}