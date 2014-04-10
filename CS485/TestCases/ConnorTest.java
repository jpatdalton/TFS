
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
		ret = client.createDir("1\\1");
		System.out.println(ret.toString());
		ret = client.createDir("1\\1\\1");
		System.out.println(ret.toString());
		
	}

}
