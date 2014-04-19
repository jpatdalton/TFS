
package testcases;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;

import message.*;
import applications.*;
import enums.Retval;

public class ConnorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		TFSClient client = new TFSClient();
		
//		Message msg = client.readFileLocally("C:\\TestData\\Test.txt");
//		
//		System.out.println(msg.retValue.toString());
//				
//		byte data [] = msg.bytes;
//		
//		client.createFile("file1.txt", 1000);
//		
//		client.append("file1.txt", data);
//		client.append("file1.txt", data);
//		
//		Message m = client.read("file1.txt");
//		
//		data = m.bytes;
//		
//		File file = new File("C:\\TestData\\Test1.txt");
//		try {
//			file.createNewFile();
//		
//		FileOutputStream output = new FileOutputStream("C:\\TestData\\Test1.txt", true);
//		output.write(data);
//		output.close();
//		}
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		/*RETVAL ret = client.createDir("1");
		System.out.println(ret.toString());

		ret = client.createFile("1\\file1", 1000);
		if(ret == RETVAL.EXISTS)
			System.out.println("FILE ALREADY EXISTS. CANNOT APPEND");

		//read in bytes
		
		String externalFile = args[0];
		System.out.println(externalFile);
		
		Path path = Paths.get(externalFile);
		byte[] data = null;
		
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			System.out.println("Could not read external file");
			e.printStackTrace();
			return;
		}
		
		client.append("1\\file1", data);
		*/
		
				/*
		RETVAL ret = client.createDir("1");
		System.out.println(ret.toString());

		ret = client.createFile("1\\test.txt", 1000);
		System.out.println(ret.toString());

		ret = client.delFile("1\\test.txt");
		System.out.println(ret.toString());
				 */
				//RETVAL ret = client.createFile("1/test.txt, 1000");


				//ret = client.createDir("1\\1");
				//System.out.println(ret.toString());
				//ret = client.createDir("1\\1\\1");
				//System.out.println(ret.toString());
				//ret = client.delDir("1\\1\\1");
				//System.out.println(ret.toString());

				/*ret = client.delDir("1\\1");
		System.out.println(ret.toString());
		ret = client.delDir("1");
		System.out.println(ret.toString());

				 */

	}

}
