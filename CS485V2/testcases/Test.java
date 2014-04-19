
package testcases;

import helpers.SerializationHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import message.Message;
import applications.*;
import enums.OPERATION;
import enums.Retval;
import enums.SENDER;

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
	
	public Test(TFSClient client) {
		tfsClient = client;	
	}

	/**
	 * A specific test case must implement this function to make a test
	 * @return
	 */
	public abstract Retval execute();

	/**
	 * Return the message to notify the result of the execution
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	protected void handleError(Retval ret) {
		message = ret.toString();
	}

	public boolean checkFileSystemLocally(String filePath){

		File file = new File(filePath);	
		return file.exists();

	}

}
