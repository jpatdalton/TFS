
package testcases;

import message.Message;
import applications.*;
import helpers.*;
import enums.Retval;

public class Test5 extends Test {
	String localPath;
	String tfsPath;

	public Test5(String masterIpAddress, int masterPort, String tfsPath, String localPath) {
		super(masterIpAddress, masterPort);
		
		this.localPath = localPath;
		this.tfsPath = tfsPath;
	}
	
	public Test5(TFSClient client, String tfsPath, String localPath ){
		super(client);
		
		this.localPath = localPath;
		this.tfsPath = tfsPath;
		
	}

	@Override
	public Retval execute() {
		
		Message returnMsg = tfsClient.read(tfsPath);
		
		byte [] data = returnMsg.bytes;
		
		if(returnMsg.retValue == Retval.NOT_FOUND || data == null)
			return Retval.NOT_FOUND;
		
		Retval ret = Application.createFileLocally(localPath);
		
		if(ret == Retval.EXISTS || ret == Retval.CLIENT_ERROR)
			return ret;
		
		ret = Application.writeFileLocally(localPath, data);
		System.out.println(ret.toString());
		

		return ret;

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
		
		Retval ret = test.execute();
		test.handleError(ret);
			
		System.out.println("WHOLE TEST: " + test.getMessage());
	}		
	

}
