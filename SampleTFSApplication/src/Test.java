import tfs.client.TFSClient;

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
	
	public static void printDelFile(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "File is removed successfully.";
			break;
		case TFSClient.NOT_FOUND:
			str = "File not found.";
			break;
		case TFSClient.CLIENT_ERROR:
			str = "Client errors. Check the command again.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error.";
			break;
		}
		
		System.out.println(str);
	}

	public static void printDelDir(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "Directory is removed successfully.";
			break;
		case TFSClient.NOT_FOUND:
			str = "Directory not found.";
			break;
		case TFSClient.NOT_EMPTY:
			str = "Directory not empty.";
			break;
		case TFSClient.CLIENT_ERROR:
			str = "Client errors. Check the command again.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error.";
			break;
		}
		
		System.out.println(str);
	}

	public static void printCreateFile(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "File created.";
			break;
		case TFSClient.EXISTED:
			str = "File existed.";
			break;
		case TFSClient.CLIENT_ERROR:
			str = "Client errors. Check the command again.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error.";
			break;
		}
		
		System.out.println(str);
	}

	public static void printCreateDir(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "Directory created.";
			break;
		case TFSClient.EXISTED:
			str = "Directory existed.";
			break;
		case TFSClient.CLIENT_ERROR:
			str = "Client errors. Check the command again.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error.";
			break;
		}
		
		System.out.println(str);
	}	
}
