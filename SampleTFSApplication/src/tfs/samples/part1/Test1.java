package tfs.samples.part1;
import java.io.IOException;

import tfs.client.TFSClient;

/**
 * Test 1: Test create-file function
 * @author Nguyen
 *
 */
public class Test1 extends Test {
	private int inputVal;
	private int count;

	public Test1(String masterIpAddress, int masterPort, int inputVal) {
		super(masterIpAddress, masterPort);
		
		this.inputVal = inputVal;
		this.count = 0;
	}

	@Override
	public int execute() {
		int ret;		// result return for each call to the TFS
		
		try {
			ret = tfsClient.createDir("1", "/");
			printCreateDir(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = TFSClient.CONN_ERROR;
			return ret;
		}
		
		if (ret != TFSClient.OK) {
			return ret;
		}
		
		this.count = 1;
		ret = createSubDirs("/1");	
		
		try {
			tfsClient.closeSockets();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	private int createSubDirs(String path) {
		int ret;
		
		String dir1 = (count++) + "";
		if (count > inputVal)
			return TFSClient.OK;
		
		try {
			ret = tfsClient.createDir(dir1, path);
			printCreateDir(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = TFSClient.CONN_ERROR;
			return ret;
		}
		
		if (ret != TFSClient.OK) {
			return ret;
		}
		
		String dir2 = (count++) + "";
		if (count > inputVal)
			return TFSClient.OK;
		
		try {
			ret = tfsClient.createDir(dir2, path);
			printCreateDir(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = TFSClient.CONN_ERROR;
			return ret;
		}
		if (ret != TFSClient.OK) {
			return ret;
		}
		
		ret = createSubDirs(path + "/" + dir1);
		if (ret != TFSClient.OK) {
			return ret;
		}
		
		ret = createSubDirs(path + "/" + dir2);
		if (ret != TFSClient.OK) {
			return ret;
		}
		
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1)
			System.out.println("Missing argument: [input_value]");
		
		int inputVal = Integer.parseInt(args[0]);
		
		String masterIpAddress = "127.0.0.1";
		int masterPort = 12231;
		
		Test1 test = new Test1(masterIpAddress, masterPort, inputVal);
		
		int ret = test.execute();
		test.handleError(ret);
			
		System.out.println(test.getMessage());
	}	
}
