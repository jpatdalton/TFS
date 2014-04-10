package helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SerializationHelper {
	public static byte[] getBytesFromFile(String inputPath) {
		byte[] data = null;
		
		try {
			Path path = Paths.get(inputPath);
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return data;
	}
}
