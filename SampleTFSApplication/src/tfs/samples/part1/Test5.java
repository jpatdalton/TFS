//package tfs.samples.part1;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import tfsclient.TFSClient;
//
//
//public class Test5 extends Test {
//	String tfsPath;
//	String localPath;
//
//	public Test5(String masterIpAddress, int masterPort, String tfsPath, String localPath) {
//		super(masterIpAddress, masterPort);
//		
//		this.tfsPath = tfsPath;
//		this.localPath = localPath;
//	}
//
//	@Override
//	public int execute() {
//		byte[] data = tfsClient.read(tfsPath);
//		if (data == null) {
//			return TFSClient.NOT_FOUND;
//		}
//		
//		File file = new File(localPath);
//		if (file.exists()) {
//			System.out.println("Local file exists");
//			return -1;
//		}
//		
//		try {
//			file.createNewFile();
//			FileOutputStream fos = new FileOutputStream(localPath);
//			
//			fos.write(data);
//			
//			fos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return TFSClient.OK;
//	}
//	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		if (args.length != 2)
//			System.out.println("Missing argument: [tfsPath] [localPath]");
//		
//		String tfsPath = args[0];
//		String localPath = args[1];
//		
//		String masterIpAddress = "";
//		int masterPort = 0;
//		
//		Test5 test = new Test5(masterIpAddress, masterPort, tfsPath, localPath);
//		
//		int ret = test.execute();
//		test.handleError(ret);
//			
//		System.out.println(test.getMessage());
//	}
//}
