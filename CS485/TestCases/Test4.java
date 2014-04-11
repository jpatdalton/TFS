
package TestCases;

import Enums.RETVAL;
import MainApplications.*;
import Helpers.*;

public class Test4 extends Test {
	String localPath;
	String tfsPath;

	public Test4(String masterIpAddress, int masterPort, String tfsPath, String localPath) {
		super(masterIpAddress, masterPort);
		
		this.localPath = localPath;
		this.tfsPath = tfsPath;
	}

	@Override
	public RETVAL execute() {
		byte[] data = SerializationHelper.getBytesFromFile(localPath);
		
		if (data == null)
			return RETVAL.CLIENT_ERROR;

		RETVAL ret = tfsClient.createDir("1");
		System.out.println(ret.toString());

		ret = tfsClient.createFile(tfsPath, 1000);
		System.out.println(ret.toString());
		
		if(ret == RETVAL.EXISTS){
			System.out.println("FILE ALREADY EXISTS. CANNOT APPEND");
			return RETVAL.EXISTS;
		}	
		
		ret = tfsClient.append(tfsPath, data);
		System.out.println(ret.toString());

		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2)
			System.out.println("Missing argument: [local_path] [tfs_path]");
		
		String localPath = args[0];
		String tfsPath = args[1];
		
		String masterIpAddress = "";
		int masterPort = 0;
		
		Test4 test = new Test4(masterIpAddress, masterPort, tfsPath, localPath);
		
		RETVAL ret = test.execute();
		test.handleError(ret);
			
		System.out.println("WHOLE TEST: " + test.getMessage());
	}		
	

}
