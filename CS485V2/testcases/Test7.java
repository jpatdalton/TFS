
package testcases;

import applications.*;
import enums.*;

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

	public Retval execute() {
		int retVal = tfsClient.getNumberOfHaystackFiles(tfsFilePath);
		
		if(retVal == -1)
			return Retval.NOT_FOUND;
		
		System.out.println("Number of Files: " + retVal);
		
		return Retval.OK;
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
		
		Retval ret = test.execute();
		test.handleError(ret);
	
	}
	
	
	
}
