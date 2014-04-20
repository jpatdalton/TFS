package tfs.samples.part1;
import java.io.IOException;

import tfs.client.TFSClient;


public class Test2 extends Test {
	private String inputPath = "";
	private int numOfFiles = 0;

	public Test2(String masterIpAddress, int masterPort, String inputPath, int n) {
		super(masterIpAddress, masterPort);
		
		this.inputPath = inputPath;
		this.numOfFiles = n;
	}

	@Override
	public int execute() {
		createFiles(inputPath);
		
		try {
			tfsClient.closeSockets();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return TFSClient.OK;
	}
	
	private void createFiles(String path) {
		int ret;
		
		// try to create n files first
		for (int i = 1; i <= numOfFiles; i++) {
			try {
				ret = tfsClient.createFile("File" + i, path, 3);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = TFSClient.CONN_ERROR;
			}
			if (ret != TFSClient.OK) {
				System.out.println("Fail to create File" + i + " in " + path + "/.");
				return;
			} else
				System.out.println("File" + i + " in " + path + "/ created.");
		}
		
		// look up for sub directories
		String[] subDirs = null;
		try {
			subDirs = tfsClient.getSubDirs(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = TFSClient.CONN_ERROR;
		}
		
		if (subDirs == null)
			return;
		
		// create files for sub-dirs
		for (String dir : subDirs)
			createFiles(dir);
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2)
			System.out.println("Missing argument: [dir_path] [num_of_files]");
		
		String dirPath = args[0];
		int numOfFiles = Integer.parseInt(args[1]);
		
		String masterIpAddress = "127.0.0.1";
		int masterPort = 12231;
		
		Test2 test = new Test2(masterIpAddress, masterPort, dirPath, numOfFiles);
		
		int ret = test.execute();
		test.handleError(ret);
			
		System.out.println(test.getMessage());
	}		
}
