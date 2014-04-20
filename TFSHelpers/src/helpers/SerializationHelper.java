package helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SerializationHelper {
	/**
	 * Get all bytes from a local path
	 * @param inputPath The local path
	 * @return The byte array for file data
	 */
	public static byte[] getBytesFromFile(String inputPath) {
		byte[] data = null;
		
		try {
			Path path = Paths.get(inputPath);
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			System.out.println("File not found. " + inputPath);
		}
		
		return data;
	}
	
	/**
	 * Write bytes to a specific offset of the file
	 * @param filePath
	 * @param data
	 * @param offset
	 * @return
	 * @throws IOException
	 */
	public static boolean writeBytesToFile(String filePath, byte[] data, int offset) throws IOException {
		byte[] filedata = getBytesFromFile(filePath);
		int filesize;
		
		if (filedata == null)
			filesize = 0;
		else
			filesize = data.length;
		
		if (filesize < offset)
			return false;
		
		// TODO the way data is appended now is not correct. will fix.
		
		byte[] newData = new byte[offset + data.length];
		
		if (filedata != null)
			System.arraycopy(filedata, 0, newData, 0, offset);
		
		System.arraycopy(data, 0, newData, offset, data.length);
		
		File file = new File(filePath);
		
		if (!file.exists())
			file.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(file);
		
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(data);
		bos.close();
		
		return true;
	}	
	
	/**
	 * Append data to the end of the file
	 * @param filePath
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static boolean writeBytesToFile(String filePath, byte[] data) throws IOException {
		int filesize;
		
		// load data from file
		byte[] filedata = getBytesFromFile(filePath);
		
		if (filedata == null)
			filesize = 0;
		else
			filesize = filedata.length;
		
		// construct the new data
		byte[] newData = new byte[filesize + data.length];		
		if (filedata != null)
			System.arraycopy(filedata, 0, newData, 0, filesize);
		
		System.arraycopy(data, 0, newData, filesize, data.length);
		
		// write new constructed data to file
		File file = new File(filePath);
		
		if (!file.exists())
			file.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(file);
		
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(newData);
		bos.close();
		
		return true;
	}		
	
	/**
	 * Convert integer to bytes
	 * @param i
	 * @return
	 */
	public static byte[] intToBytes(int i) {
		 byte[] result = ByteBuffer.allocate(4).putInt(i).array();
		 
		 return result;		
	}
	
	public static int bytesToInt(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		return buffer.getInt();
	}
}
