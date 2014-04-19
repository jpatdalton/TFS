package applications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import testcases.*;
import message.*;
import enums.Retval;

public class FunctionCommandLine {

	public static void printErrorHandle(Retval ret){

		System.out.println(ret.toString());

	}

	public static void main(String[] args) {		
		TFSClient client = new TFSClient();
		String dirPath;
		String filePath;
		String localPath;
		String tfsPath;
		Message msg;
		Test1 t1;
		Test2 t2;
		Test4 t4;
		Test5 t5;		
		
		Retval ret;

		//  open up standard input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";

		printHelper();
		
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

			for(int i = 0; i < strs.length; i++){
				strs[i] = Application.NormalizeString(strs[i]);
				
			}
			
			System.out.println(strs[1]);
			
			switch (strs[0]) {
			case "createdir":
				if (strs.length != 2) {
					printHelper();
					break;
				}

				dirPath = strs[1];
				ret = client.createDir(dirPath);
				printErrorHandle(ret);
				break;
			case "createfile":
				if (strs.length != 2) {
					printHelper();
					break;
				}

				filePath = strs[1];
				ret = client.createFile(filePath, 1000);
				printErrorHandle(ret);

				break;
			case "deldir":
				if (strs.length != 2) {
					printHelper();
					break;
				}

				dirPath = strs[1];
				ret = client.delDir(dirPath);
				printErrorHandle(ret);

				break;
			case "delfile":
				if (strs.length != 2) {
					printHelper();
					break;
				}

				filePath = strs[1];
				ret = client.delFile(filePath);
				printErrorHandle(ret);
				break;

			case "appendtolocal":
				if(strs.length != 3){
					printHelper();
					break;
				}
				tfsPath = strs[1];
				localPath = strs[2];
				msg = client.read(tfsPath);
				if(msg.retValue == Retval.OK)
				{
					ret = Application.writeFileLocally(localPath, msg.bytes);
					printErrorHandle(msg.retValue);
				}
				else{
					printErrorHandle(msg.retValue);
				}
				break;
				
			case "haystackappend":
				if(strs.length != 3){
					printHelper();
					break;
				}
				
				localPath = strs[1];
				tfsPath = strs[2];

				msg = Application.readFileLocally(localPath);
				if(msg.retValue == Retval.OK)
				{
					ret = client.haystackAppend(tfsPath, msg.bytes);
					printErrorHandle(ret);
				}
				else{
					printErrorHandle(msg.retValue);
				}
				break;
				
			case "appendtotfs":
				if(strs.length != 3){
					printHelper();
					break;
				}

				localPath = strs[1];
				tfsPath = strs[2];

				msg = Application.readFileLocally(localPath);
				if(msg.retValue == Retval.OK)
				{
					ret = client.append(tfsPath, msg.bytes);
					printErrorHandle(msg.retValue);
				}
				else{
					printErrorHandle(msg.retValue);
				}
				break;
				
			case "counthaystack":
				if(strs.length != 2){
					printHelper();
					break;
				}
				
				tfsPath = strs[1];
				
				int num = client.getNumberOfHaystackFiles(tfsPath);
				
				if(num == -1){
					printErrorHandle(Retval.NOT_FOUND);
				}
				if(num == -2){
					System.out.println("Tried to use count on non-haystack file");
				}
				else{
					printErrorHandle(Retval.OK);
					System.out.println(num + " haystack files");
				}
				break;
				
			case "createlocal":
				if(strs.length != 2){
					printHelper();
					break;
				}
				
				localPath = strs[1];
				ret = Application.createFileLocally(localPath);
				printErrorHandle(ret);
				
				break;
				
			case "test1":
				if(strs.length != 2){
					printHelper();
					break;
				}
				
				int inputVal = Integer.parseInt(strs[1]);
				
				t1 = new Test1(client, inputVal);
				ret = t1.execute();
				
			
				
				break;
				
			case "test2":
				if(strs.length != 3){
					printHelper();
					break;
				}
				
				tfsPath = strs[1];
				int numFile = Integer.parseInt(strs[2]);
				
				t2 = new Test2(client, tfsPath, numFile);
				t2.execute();
				
				break;
				
				
			case "test4":
				if(strs.length != 3){
					printHelper();
					break;
				}
				
				localPath = strs[1];
				tfsPath = strs[2];
				
				t4 = new Test4(client, tfsPath, localPath);
				ret = t4.execute();
				
				System.out.println(ret.toString());
				
				break;
				
			case "test5":
				
				if(strs.length != 3){
					printHelper();
					break;
				}
				
				tfsPath = strs[1];
				localPath = strs[2];
				
				t5 = new Test5(client, tfsPath, localPath);
				t5.execute();
				
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
		System.out.println("1. Create a tfs directory: createdir [dirpath]");
		System.out.println("2. Create a tfs file: createfile [filepath]");
		System.out.println("3. Delete a tfs directory: deldir [dirpath]");
		System.out.println("4. Delete a tfs file: delfile [filepath]");
		System.out.println("5. Append From Local to TFS: appendtotfs [localPath] [tfsPath]");
		System.out.println("6. Append From TFS to Local: appendtolocal [tfsPath] [localPath]");
		System.out.println("7. Haystack Append From Local to TFS: haystackappend [localPath] [tfsPath]");
		System.out.println("8. Count Files in Haystack: counthaystack [tfsPath]");
		System.out.println("***************TESTS*****************");
		System.out.println("test1 [inputVal]");
		System.out.println("test2 [tfsDirPath] [numFiles]");
		System.out.println("Check Test 3 by using deldir ^");
		System.out.println("test4 [localPath] [tfsFilePath]");
		System.out.println("test5 [tfsFilePath] [localPath]");
		System.out.println("Check Test 6 by using haystackappend ^");
		System.out.println("Check Test 7 by using counthaystack ^");
		
		
		System.out.println();		
	}
}
