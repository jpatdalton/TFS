
package testcases;

import java.util.ArrayList;

import applications.*;
import enums.Retval;


public class Test2 extends Test {
	private String inputPath = "";
	private int numOfFiles = 0;

	public Test2(String masterIpAddress, int masterPort, String inputPath, int n) {
		super(masterIpAddress, masterPort);
		
		this.inputPath = inputPath;
		this.numOfFiles = n;
	}

	public Test2(TFSClient client, String inputPath, int n){
		super(client);
		
		this.inputPath = inputPath;
		this.numOfFiles = n;
	}
	
	@Override
	public Retval execute() {
		createFiles(inputPath);
		
		return Retval.OK;
	}
	
	private void createFiles(String path) {
		Retval ret;
		
		// try to create n files first
		for (int i = 1; i <= numOfFiles; i++) {
			ret = tfsClient.createFile(path + "\\File" + i, 1000);
			if (ret != Retval.OK) {
				System.out.println("Fail to create File" + i + " in " + path + "/.");
			} else
				System.out.println("File" + i + " in " + path + "/ created.");
		}
		
		// look up for sub directories
		ArrayList<String> subDirs = Application.getSubdirectories("C:\\CS485\\" + path);
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
		
		String masterIpAddress = "";
		int masterPort = 0;
		
		Test2 test = new Test2(masterIpAddress, masterPort, dirPath, numOfFiles);
		
		Retval ret = test.execute();
		test.handleError(ret);
			
		System.out.println(test.getMessage());
	}		
}
