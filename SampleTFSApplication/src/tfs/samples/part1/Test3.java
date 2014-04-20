package tfs.samples.part1;
//import tfsclient.TFSClient;
//
//
//public class Test3 extends Test {
//	String inputPath;
//	
//	public Test3(String masterIpAddress, int masterPort, String inputPath) {
//		super(masterIpAddress, masterPort);
//
//		this.inputPath = inputPath;
//	}
//
//	@Override
//	public int execute() {
//		return tfsClient.delDir(inputPath, TFSClient.DEL_ALL);
//	}
//	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		if (args.length != 1)
//			System.out.println("Missing argument: [dir_path]");
//		
//		String dirPath = args[0];		
//		
//		String masterIpAddress = "";
//		int masterPort = 0;
//		
//		Test3 test = new Test3(masterIpAddress, masterPort, dirPath);
//		
//		int ret = test.execute();
//		test.handleError(ret);
//			
//		System.out.println(test.getMessage());
//	}		
//
//}
