package tfs.samples.part2;

import java.io.IOException;

import tfs.samples.part1.Test;
import tfs.client.TFSClient;
import helpers.SerializationHelper;

/**
 * Test 6: Append the size and content of a file stored on the local machine in a target TFS file specified by its path
 * @author Nguyen
 *
 */
public class Unit6 extends Test {
	String localPath;
	String tfsPath;

	public Unit6(String masterIpAddress, int masterPort, String localPath, String tfsPath) {
		super(masterIpAddress, masterPort);
		
		this.localPath = localPath;
		this.tfsPath = tfsPath;
	}

	@Override
	public int execute() {
		byte[] data = null;
		data = SerializationHelper.getBytesFromFile(localPath);
		
		if (data == null)
			return TFSClient.CLIENT_ERROR;
		
		int ret = TFSClient.CLIENT_ERROR;
		try {
			ret = tfsClient.append(tfsPath, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		
		Unit6 test = new Unit6(masterIpAddress, masterPort, localPath, tfsPath);
		
		int ret = test.execute();
		test.handleError(ret);
			
		System.out.println(test.getMessage());
	}
}
