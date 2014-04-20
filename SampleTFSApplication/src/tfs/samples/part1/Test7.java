package tfs.samples.part1;
import java.io.IOException;

/**
 * 
 * @author Nguyen
 *
 */
public class Test7 extends Test {
	String tfsFilePath;

	public Test7(String masterIpAddress, int masterPort, String tfsFilePath) {
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
		
		Test7 test = new Test7(masterIpAddress, masterPort, filePath);
		
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
