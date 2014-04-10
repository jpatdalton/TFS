
package TestCases;

import MainApplications.*;
import Enums.*;

/**
 * Test 1: Test create-file function
 * @author Nguyen
 *
 */
public class Test1 extends Test {
	private int inputVal;
	private int count;

	public Test1(String masterIpAddress, int masterPort, int inputVal) {
		super(masterIpAddress, masterPort);
		
		this.inputVal = inputVal;
		this.count = 0;
	}

	@Override
	public RETVAL execute() {
		RETVAL ret;		// result return for each call to the TFS
		
		ret = tfsClient.createDir("1/");
		if (ret != RETVAL.OK) {
			return ret;
		}
		
		this.count = 1;
		ret = createSubDirs("/1");	
		
		return ret;
	}
	
	private RETVAL createSubDirs(String path) {
		RETVAL ret;
		
		String dir1 = (count++) + "";
		if (count > inputVal)
			return RETVAL.OK;
		
		ret = tfsClient.createDir(path + "/" + dir1);
		if (ret != RETVAL.OK) {
			return ret;
		}
		
		String dir2 = (count++) + "";
		if (count > inputVal)
			return RETVAL.OK;
		
		ret = tfsClient.createDir(path +"/" + dir2);
		if (ret != RETVAL.OK) {
			return ret;
		}
		
		ret = createSubDirs(path + "/" + dir1);
		if (ret != RETVAL.OK) {
			return ret;
		}
		
		ret = createSubDirs(path + "/" + dir2);
		if (ret != RETVAL.OK) {
			return ret;
		}
		
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1)
			System.out.println("Missing argument: [input_value]");
		
		int inputVal = Integer.parseInt(args[0]);
		
		String masterIpAddress = "";
		int masterPort = 0;
		
		Test1 test = new Test1(masterIpAddress, masterPort, inputVal);
		
		RETVAL ret = test.execute();
		test.handleError(ret);
			
		System.out.println(test.getMessage());
	}	
}
