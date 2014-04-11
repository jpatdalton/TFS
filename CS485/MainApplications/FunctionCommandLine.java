package MainApplications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Enums.RETVAL;

public class FunctionCommandLine {
	
	public static void main(String[] args) {		
		TFSClient client = new TFSClient();
		String dirPath;
		String filePath;	
		RETVAL ret;
		
		//  open up standard input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		
		while (true) {
			try {
				System.out.print("> ");
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				line = "";
			}
			
			String[] strs = line.split(" ");
			
			switch (strs[0]) {
			case "createdir":
				if (strs.length != 2) {
					printHelper();
					return;
				}
				
				dirPath = strs[1];
				ret = client.createDir(dirPath);
				break;
			case "createfile":
				if (strs.length != 2) {
					printHelper();
					return;
				}
				
				filePath = strs[1];
				ret = client.createFile(filePath);			
				break;
			case "deldir":
				if (strs.length != 2) {
					printHelper();
					return;
				}
				
				dirPath = strs[1];
				ret = client.delDir(dirPath);	
				break;
			case "delfile":
				if (strs.length != 2) {
					printHelper();
					return;
				}
				
				filePath = strs[1];
				ret = client.delFile(filePath);				
				break;
			default:
				printHelper();
				break;
			}
		}
	}
	
	private static void printHelper() {
		System.out.println("TFS Function Command Line");
		System.out.println("Using one of these command line to test for the features of TFS");
		System.out.println("1. Create a directory: createdir [dirpath]");
		System.out.println("2. Create a file: createfile [filepath]");
		System.out.println("3. Delete a directory: deldir [dirpath]");
		System.out.println("4. Delete a file: delfile [filepath]");
		System.out.println();		
	}
}
