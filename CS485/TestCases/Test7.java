
package TestCases;

import Enums.*;
import MainApplications.*;

/**
 * 
 * @author Nguyen
 *
 */
public class Test7 extends Test {
	String tfsFilePath;

	public Test7(String masterIpAddress, int masterPort, String tfsFilePath) {
		super(masterIpAddress, masterPort);
		
		this.tfsFilePath = tfsFilePath;
	}

	public RETVAL execute() {
		int retVal = tfsClient.getNumberOfHaystackFiles(tfsFilePath);
		
		if(retVal == -1)
			return RETVAL.NOT_FOUND;
		
		System.out.println("Number of Files: " + retVal);
		
		return RETVAL.OK;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1)
			System.out.println("Missing argument: [tfsFilePath]");
		
		String filePath = args[0];
		
		String masterIpAddress = "";
		int masterPort = 0;
		
		Test7 test = new Test7(masterIpAddress, masterPort, filePath);
		
		RETVAL ret = test.execute();
		test.handleError(ret);
	
	}
	
	
	
}
