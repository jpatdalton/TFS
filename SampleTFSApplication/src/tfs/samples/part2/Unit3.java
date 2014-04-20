package tfs.samples.part2;

import java.io.IOException;

import tfs.client.TFSClient;
import tfs.samples.part1.Test;

public class Unit3 extends Test {
	String inputPath;
	
	public Unit3(String masterIpAddress, int masterPort, String inputPath) {
		super(masterIpAddress, masterPort);

		this.inputPath = inputPath;
	}

	@Override
	public int execute() {
		int ret;
		
		try {
			ret = tfsClient.delDir(inputPath, TFSClient.DEL_ALL);
			printDelDir(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = TFSClient.CLIENT_ERROR;
		}
		
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1)
			System.out.println("Missing argument: [dir_path]");
		
		String dirPath = args[0];		
		
		String masterIpAddress = "";
		int masterPort = 0;
		
		Unit3 test = new Unit3(masterIpAddress, masterPort, dirPath);
		
		int ret = test.execute();
		test.handleError(ret);
			
		System.out.println(test.getMessage());
	}		

}
