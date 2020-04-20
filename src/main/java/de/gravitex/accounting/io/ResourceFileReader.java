package de.gravitex.accounting.io;

import java.io.File;
import java.net.URL;

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
}