
package testcases;

import java.util.ArrayList;
import java.util.List;

import applications.*;
import enums.*;

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
	
	public Test1(TFSClient client, int inputVal){
		super(client);
		
		this.inputVal = inputVal;
		this.count = 2;
		
	}

	@Override
	public Retval execute() {
		Retval ret;		// result return for each call to the TFS
		
		ret = tfsClient.createDir("1");
		if (ret == Retval.ERROR) {
			return ret;
		}
		
		this.count = 1;
		List<String> subPath = new ArrayList<String>();
		subPath.add("1");
		ret = createSubDirs(subPath);	
		
		return ret;
	}
	
	private Retval createSubDirs(List<String> paths) {
		Retval ret = Retval.DEFAULT;
		String path = "";
		int dir;
		
		List<String> subPaths = new ArrayList<String>();
		
		for (int i = 0; i < paths.size(); i++) {
			for (int j = 0; j < 2; j++) {
				dir = (++count);
				if (dir > inputVal)
					return Retval.OK;
				
				path = paths.get(i) + "\\" + dir;
				ret = tfsClient.createDir(path);
				if (ret != Retval.OK)
					return ret;
				else
					paths.add(path);
			}
		}
		
		if (count < inputVal)
			ret = createSubDirs(subPaths);
		
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Missing argument: [input_value]");
			return;
		}
		
		int inputVal = Integer.parseInt(args[0]);
		
		String masterIpAddress = "127.0.0.1";
		int masterPort = 3434;
		
		Test1 test = new Test1(masterIpAddress, masterPort, inputVal);
		
		Retval ret = test.execute();
		test.handleError(ret);
			
		System.out.println(test.getMessage());
	}	
}
