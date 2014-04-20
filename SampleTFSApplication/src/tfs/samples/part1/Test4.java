package tfs.samples.part1;
//import tfsclient.TFSClient;
//import helpers.*;
//
//public class Test4 extends Test {
//	String localPath;
//	String tfsPath;
//
//	public Test4(String masterIpAddress, int masterPort, String tfsPath, String localPath) {
//		super(masterIpAddress, masterPort);
//		
//		this.localPath = localPath;
//		this.tfsPath = tfsPath;
//	}
//
//	@Override
//	public int execute() {
//		byte[] data = SerializationHelper.getBytesFromFile(localPath);
//		
//		if (data == null)
//			return TFSClient.CLIENT_ERROR;
//		
//		String[] paths = tfsPath.split("/");
//		String fileName = paths[paths.length - 1];
//		String dirPath = tfsPath.substring(0, tfsPath.length() - fileName.length());
//		
//		int ret = tfsClient.createFile(fileName, dirPath);
//		if (ret != TFSClient.OK)
//			return ret;
//		
//		ret = tfsClient.append(tfsPath, data);
//
//		return ret;
//	}
//	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		if (args.length != 2)
//			System.out.println("Missing argument: [local_path] [tfs_path]");
//		
//		String localPath = args[0];
//		String tfsPath = args[1];
//		
//		String masterIpAddress = "";
//		int masterPort = 0;
//		
//		Test4 test = new Test4(masterIpAddress, masterPort, localPath, tfsPath);
//		
//		int ret = test.execute();
//		test.handleError(ret);
//			
//		System.out.println(test.getMessage());
//	}		
//	
//
//}
