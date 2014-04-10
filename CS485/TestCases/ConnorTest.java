
package TestCases;

import Enums.RETVAL;
import MainApplications.*;


public class ConnorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TFSClient client = new TFSClient();
		
		RETVAL ret = client.createDir("1");
		System.out.println(ret.toString());
	
		//RETVAL ret = client.createFile("1/test.txt, 1000");
		
		
		ret = client.createDir("1\\1");
		System.out.println(ret.toString());
		ret = client.createDir("1\\1\\1");
		System.out.println(ret.toString());
		ret = client.delDir("1\\1\\1");
		System.out.println(ret.toString());
		
		/*ret = client.delDir("1\\1");
		System.out.println(ret.toString());
		ret = client.delDir("1");
		System.out.println(ret.toString());
		
		*/
		
	}

}
