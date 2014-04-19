
package testcases;

import message.*;
import applications.*;
import helpers.*;
import enums.Retval;
public class Test4 extends Test {
	String localPath;
	String tfsPath;

	public Test4(String masterIpAddress, int masterPort, String tfsPath, String localPath) {
		super(masterIpAddress, masterPort);
		
		this.localPath = localPath;
		this.tfsPath = tfsPath;
	}
	
	public Test4(TFSClient client, String tfsPath, String localPath){
		super(client);
		
		this.localPath = localPath;
		this.tfsPath = tfsPath;
		
	}

	@Override
	public Retval execute() {
		
		Message msg = Application.readFileLocally(localPath);
		
		byte[] data = msg.bytes;
		
		if(msg.retValue != Retval.OK)
			return msg.retValue;
	
		Retval ret = tfsClient.createFile(tfsPath, 1000);
		
		if(ret != Retval.OK)
			return ret;
		
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
		
		Retval ret = test.execute();
		test.handleError(ret);
			
		System.out.println("WHOLE TEST: " + test.getMessage());
	}		
	

}
