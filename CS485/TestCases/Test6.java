
package TestCases;

import Helpers.*;
import MainApplications.*;
import Enums.*;
import Message.*;

/**
 * Test 6: Append the size and content of a file stored on the local machine in a target TFS file specified by its path
 * @author Nguyen
 *
 */
public class Test6 extends Test {
	String localPath;
	String tfsPath;

	public Test6(String masterIpAddress, int masterPort, String localPath, String tfsPath) {
		super(masterIpAddress, masterPort);
		
		this.localPath = localPath;
		this.tfsPath = tfsPath;
	}

	@Override
	public RETVAL execute() {
		
		Message msg = tfsClient.readFileLocally(localPath);
		
		if (msg.bytes == null)
			return RETVAL.NOT_FOUND;
		
		tfsClient.createFile(tfsPath, 1000);
		
		
		
		int ret = tfsClient.append(tfsPath, data);

		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2)
			System.out.println("Missing argument: [localPath] [tfsPath]");
		
		String localPath = args[0];
		String tfsPath = args[1];
		
		String masterIpAddress = "";
		int masterPort = 0;
		
		Test6 test = new Test6(masterIpAddress, masterPort, localPath, tfsPath);
		
		RETVAL ret = test.execute();
		test.handleError(ret);
			
		System.out.println(test.getMessage());
	}
}
