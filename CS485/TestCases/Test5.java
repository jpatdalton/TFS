
package TestCases;

import Enums.RETVAL;
import MainApplications.*;
import Message.Message;
import Helpers.*;

public class Test5 extends Test {
	String localPath;
	String tfsPath;

	public Test5(String masterIpAddress, int masterPort, String tfsPath, String localPath) {
		super(masterIpAddress, masterPort);
		
		this.localPath = localPath;
		this.tfsPath = tfsPath;
	}

	@Override
	public RETVAL execute() {
		
		/**************** TEST 4 ********************/
		
		byte[] data = SerializationHelper.getBytesFromFile("C:\\TestData\\smiley.jpg");
		
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
		
		ret = tfsClient.append("C:\\CS485\\" + tfsPath, data);
		System.out.println(ret.toString());
		
		/****************** END TEST 4 ****************/
		
		
		
		Message returnMsg = tfsClient.read(tfsPath);
		
		data = returnMsg.bytes;
		//byte[] data = returnMsg.bytes;
		
		if(returnMsg.retValue == RETVAL.NOT_FOUND || data == null)
			return RETVAL.NOT_FOUND;

		if(tfsClient.checkFileSystemLocally(localPath))
			return RETVAL.EXISTS;
		
		//RETVAL ret = tfsClient.append(localPath, data);
		
		ret = tfsClient.createFileLocally(localPath);
		
		if(ret == RETVAL.EXISTS || ret == RETVAL.CLIENT_ERROR)
			return ret;
		
		ret = tfsClient.writeFileLocally(localPath, data);

		return ret;
		
		
	/*	RETVAL ret = tfsClient.createDir("1");
		System.out.println(ret.toString());

		ret = tfsClient.createFile(tfsPath, 1000);
		System.out.println(ret.toString());
		
		if(ret == RETVAL.EXISTS){
			System.out.println("FILE ALREADY EXISTS. CANNOT APPEND");
			return RETVAL.EXISTS;
		}	
		
		ret = tfsClient.append(tfsPath, data);
		System.out.println(ret.toString());

		return ret; */
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2){
			System.out.println("Missing argument: [local_path] [tfs_path]");
			return;
		}
		
		String tfsPath = args[0];
		String localPath = args[1];
		
		
		String masterIpAddress = "";
		int masterPort = 0;
		
		Test5 test = new Test5(masterIpAddress, masterPort, tfsPath, localPath);
		
		RETVAL ret = test.execute();
		test.handleError(ret);
			
		System.out.println("WHOLE TEST: " + test.getMessage());
	}		
	

}
