
package TestCases;

import Enums.RETVAL;
import MainApplications.*;

/**
 * Represents a test in general
 * @author Nguyen
 *
 */
public abstract class Test {
	protected String message;			// message for the test
	protected TFSClient tfsClient;		// TFS client instance used for this test
	
	public Test(String masterIpAddress, int masterPort) {
		tfsClient = new TFSClient();	
	}
	
	/**
	 * A specific test case must implement this function to make a test
	 * @return
	 */
	public abstract RETVAL execute();
	
	/**
	 * Return the message to notify the result of the execution
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	
	protected void handleError(RETVAL ret) {
		message = ret.toString();
	}
}
