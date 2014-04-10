import tfsclient.TFSClient;

/**
 * Represents a test in general
 * @author Nguyen
 *
 */
public abstract class Test {
	protected String message;			// message for the test
	protected TFSClient tfsClient;		// TFS client instance used for this test
	
	public Test(String masterIpAddress, int masterPort) {
		tfsClient = new TFSClient(masterIpAddress, masterPort);	
	}
	
	/**
	 * A specific test case must implement this function to make a test
	 * @return
	 */
	public abstract int execute();
	
	/**
	 * Return the message to notify the result of the execution
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	
	protected void handleError(int ret) {
		if (ret == TFSClient.EXISTED)
			message = "EXISTED";
		else if (ret == TFSClient.CLIENT_ERROR)
			message = "CLIENT ERROR";
		else if (ret == TFSClient.SERVER_ERROR)
			message = "SERVER ERROR";
		else if (ret == TFSClient.OK)
			message = "OK";
	}
}
