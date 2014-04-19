package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SerializationHelper {
	public static byte[] getBytesFromFile(String inputPath) {
		byte[] data = null;
		
		try {
			Path path = Paths.get(inputPath);
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			
			return data;
			
		}
		catch(InvalidPathException ex){
			
			return null;
			
		}
		
		return data;
	}
	
	public static byte[] getBytesFromFileWithOffset(String inputPath) {
		byte[] data = null;
		
		byte header [] = new byte [4];
		try {
			
			File file = new File(inputPath);
			FileInputStream input = new FileInputStream(file);
			input.read(header);
			
			ByteBuffer bb = ByteBuffer.wrap(header);
			int dataSize = bb.getInt();
			data = new byte[dataSize];
			input.read(data);
			input.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		return data;
	}
	
}
