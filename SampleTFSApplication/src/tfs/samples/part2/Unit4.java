package tfs.samples.part2;

import java.io.IOException;

import tfs.client.TFSClient;
import helpers.*;
import tfs.samples.part1.Test;

public class Unit4 extends Test {
	String localPath;
	String tfsPath;

	public Unit4(String masterIpAddress, int masterPort, String tfsPath, String localPath) {
		super(masterIpAddress, masterPort);
		
		this.localPath = localPath;
		this.tfsPath = tfsPath;
	}

	@Override
	public int execute() {
		byte[] data;
		
		data = SerializationHelper.getBytesFromFile(localPath);
		
		if (data == null)
			return TFSClient.NOT_FOUND;
		
		String[] paths = tfsPath.split("/");
		String fileName = paths[paths.length - 1];
		String dirPath = tfsPath.substring(0, tfsPath.length() - fileName.length());
		
		int ret = TFSClient.CLIENT_ERROR;
		try {
			ret = tfsClient.createFile(fileName, dirPath, 3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (ret != TFSClient.OK)
			return ret;
		
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
			System.out.println("Missing argument: [local_path] [tfs_path]");
		
		String localPath = args[0];
		String tfsPath = args[1];
		
		String masterIpAddress = "";
		int masterPort = 0;
		
		Unit4 test = new Unit4(masterIpAddress, masterPort, localPath, tfsPath);
		
		int ret = test.execute();
		test.handleError(ret);
			
		System.out.println(test.getMessage());
	}		
	

}
