package tfs.samples.part2;

import java.io.IOException;
import tfs.samples.part1.Test;

/**
 * 
 * @author Nguyen
 *
 */
public class Unit7 extends Test {
	String tfsFilePath;

	public Unit7(String masterIpAddress, int masterPort, String tfsFilePath) {
		super(masterIpAddress, masterPort);
		
		this.tfsFilePath = tfsFilePath;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1)
			System.out.println("Missing argument: [tfsFilePath]");
		
		String filePath = args[0];
		
		String masterIpAddress = "";
		int masterPort = 0;
		
		Unit7 test = new Unit7(masterIpAddress, masterPort, filePath);
		
		test.execute();
	}

	@Override
	public int execute() {
		int replicaCount = -1;
		try {
			replicaCount = tfsClient.count(tfsFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (replicaCount >= 0)
			System.out.println("Replica count: " + replicaCount);
		
		return 0;
	}

}
