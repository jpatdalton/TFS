
package TestCases;

import Enums.RETVAL;
import MainApplications.*;

import java.io.IOException;
import java.nio.file.*;


public class ConnorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		TFSClient client = new TFSClient();

		RETVAL ret = client.createDir("1");
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
