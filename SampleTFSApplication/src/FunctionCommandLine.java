import helpers.SerializationHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import tfs.client.*;

public class FunctionCommandLine {
	
	public static void main(String[] args) {	
		TFSClient.trace = true;
		TFSClient client = new TFSClient("127.0.0.1", 12231);
		System.out.println("Function Command Line for Tiny Bit Distributed System");
		System.out.println("For help, type help.");
		
		String dirName, dirPath;
		String fileName, filePath;
		int ret;
		byte[] data = null;
		
	    //  open up standard input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command = "";
		
		while (true) {
			// read command from user input
			System.out.print("> ");
			try {
				command = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
				command = "";		// reset command if fails to read
			}
			
			// analyze the command
			String[] cmds = command.split(" ");			
			if (cmds.length == 0 || cmds[0] == "help") {
				printHelper();
			}
			
			switch (cmds[0]) {
			case TFSClient.CREATE_DIR:
				if (cmds.length != 3) {
					printHelper();
					continue;
				}
				
				dirName = cmds[1];
				dirPath = cmds[2];
				try {
					ret = client.createDir(dirName, dirPath);
					printCreateDir(ret);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case TFSClient.CREATE_FILE:
				if (cmds.length < 3) {
					printHelper();
					continue;
				}
				
				fileName = cmds[1];
				dirPath = cmds[2];
				
				int numOfReplicas = 3;
				
				if (cmds.length == 4) {
					numOfReplicas = Integer.parseInt(cmds[3]);
				}
				
				try {
					ret = client.createFile(fileName, dirPath, numOfReplicas);
					printCreateFile(ret);
				} catch (IOException e) {
					e.printStackTrace();
				}			
				break;
			case TFSClient.DELETE_DIR:
				if (cmds.length != 3) {
					printHelper();
					continue;
				}
				
				dirPath = cmds[1];
				try {
					int mode = Integer.parseInt(cmds[2]);
					ret = client.delDir(dirPath, mode);
					printDelDir(ret);
				} catch (Exception e) {
					e.printStackTrace();
				}					
				break;
			case TFSClient.DELETE_FILE:
				if (cmds.length != 2) {
					printHelper();
					continue;
				}
				
				filePath = cmds[1];
				try {
					ret = client.delFile(filePath);
					printDelFile(ret);
				} catch (IOException e) {
					e.printStackTrace();
				}					
				break;
			case TFSClient.GET_FILE_INFO:
				if (cmds.length != 2) {
					printHelper();
					continue;				
				}
				
				filePath = cmds[1];
				try {
					ret = client.getFileInfo(filePath);
				} catch (IOException e) {
					e.printStackTrace();
				}					
				break;
			case TFSClient.GET_DIR_INFO:
				if (cmds.length != 2) {
					printHelper();
					continue;				
				}
				
				dirPath = cmds[1];
				try {
					client.getDirInfo(dirPath);
				} catch (IOException e) {
					e.printStackTrace();
				}					
				break;			
			case TFSClient.APPEND:
				if (cmds.length != 3) {
					printHelper();
					continue;
				}
				
				String localPath = cmds[1];
				String tfsPath = cmds[2];
				
				data = SerializationHelper.getBytesFromFile(localPath);
				
				if (data == null) {
					ret = TFSClient.NOT_FOUND;
					printAppend(ret);
					continue;
				} else {
					ret = TFSClient.OK;
				}
				
				try {
					ret = client.append(tfsPath, data);
				} catch (IOException e) {
					e.printStackTrace();
					ret = TFSClient.SERVER_ERROR;
				}
				
				printAppend(ret);
				break;
			case TFSClient.READ:
				if (cmds.length < 3) {
					printHelper();
					continue;					
				}
				
				filePath = cmds[1];
				int offset = Integer.parseInt(cmds[2]);
				
				try {
					data = client.read(filePath, offset, -1);
					
					if (data == null)
						ret = TFSClient.NOT_FOUND;
					else {
						SerializationHelper.writeBytesToFile("D:\\temp.jpg", data);
						ret = TFSClient.OK;
					}
					
					printRead(ret);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case TFSClient.READALL:
				if (cmds.length != 2) {
					printHelper();
					continue;
				}
				
				filePath = cmds[1];
				
				try {
					data = client.readall(filePath);
					if (data == null)
						ret = TFSClient.NOT_FOUND;
					else {
						SerializationHelper.writeBytesToFile("D:\\temp.jpg", data);
						ret = TFSClient.OK;
					}		
					
					printRead(ret);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case TFSClient.COUNT:
				if (cmds.length != 2) {
					printHelper();
					continue;
				}
				
				filePath = cmds[1];
				int val = -1;
				try {
					val = client.count(filePath);
					if (val == -1)
						ret = TFSClient.NOT_FOUND;
					else {
						System.out.println("Data item count: " + val);
						ret = TFSClient.OK;
					}
					printCount(ret);
				} catch (IOException e) {
					e.printStackTrace();
				}				
				break;
			}
		}
	}
	
	private static void printCount(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "Count success.";
			break;
		case TFSClient.NOT_FOUND:
			str = "TFS file not found.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error";
			break;
		default:
			str = "Client error";
			break;
		}
		
		System.out.println(str);	
	}

	private static void printRead(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "Read success.";
			break;
		case TFSClient.NOT_FOUND:
			str = "Local file or TFS file not found.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error";
			break;
		default:
			str = "Client error";
			break;
		}
		
		System.out.println(str);		
	}

	private static void printAppend(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "Append success.";
			break;
		case TFSClient.NOT_FOUND:
			str = "Local file or TFS file not found.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error";
			break;
		default:
			str = "Client error";
			break;
		}
		
		System.out.println(str);
	}
	
	private static void printDelFile(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "File is removed successfully.";
			break;
		case TFSClient.NOT_FOUND:
			str = "File not found.";
			break;
		case TFSClient.CLIENT_ERROR:
			str = "Client errors. Check the command again.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error.";
			break;
		}
		
		System.out.println(str);
	}

	private static void printDelDir(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "Directory is removed successfully.";
			break;
		case TFSClient.NOT_FOUND:
			str = "Directory not found.";
			break;
		case TFSClient.NOT_EMPTY:
			str = "Directory not empty.";
			break;
		case TFSClient.CLIENT_ERROR:
			str = "Client errors. Check the command again.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error.";
			break;
		}
		
		System.out.println(str);
	}

	private static void printCreateFile(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "File created.";
			break;
		case TFSClient.EXISTED:
			str = "File existed.";
			break;
		case TFSClient.CLIENT_ERROR:
			str = "Client errors. Check the command again.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error.";
			break;
		}
		
		System.out.println(str);
	}

	private static void printCreateDir(int ret) {
		String str = "";
		
		switch (ret) {
		case TFSClient.OK:
			str = "Directory created.";
			break;
		case TFSClient.EXISTED:
			str = "Directory existed.";
			break;
		case TFSClient.CLIENT_ERROR:
			str = "Client errors. Check the command again.";
			break;
		case TFSClient.SERVER_ERROR:
			str = "Server error.";
			break;
		}
		
		System.out.println(str);
	}

	private static void printHelper() {
		System.out.println("TFS Function Command Line");
		System.out.println("Using one of these command line to test for the features of TFS");
		System.out.println("1. Create a directory: createdir [dirpath]");
		System.out.println("2. Create a file: createfile [filepath]");
		System.out.println("3. Delete a directory: deldir [dirpath] [mode] (mode -- 0 for DEL_ONLY, 1 for DEL_ALL");
		System.out.println("4. Delete a file: delfile [filepath]");
		System.out.println("5. Get dir info: dirinfo [dirpath]");
		System.out.println("6. Get file info: fileinfo [filepath]");
		System.out.println("7. Append to the end of file: append [localpath] [tfspath]");
//		System.out.println("8. Write to a specific offset: write [localpath] [tfspath] [offset]");
		System.out.println("8. Read data: read [tfspath] [offset]");
		System.out.println("9. Read all data: readall [tfspath]");
		System.out.println("10. Count data item: count [tfspath]");	
	}
}
