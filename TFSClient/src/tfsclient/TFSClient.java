package tfsclient;

/**
 * Client component for the TFS
 * @author Nguyen
 *
 */
public class TFSClient {
	private String masterIpAddress = "127.0.0.1";
	private int masterPort = 12372;
	
	public static final int OK = 0;
	public static final int EXISTED = 1;
	public static final int CLIENT_ERROR = 2;
	public static final int SERVER_ERROR = 3;
	public static final int NOT_FOUND = 4;
	
	public static final int DEL_ONLY = 0;
	public static final int DEL_ALL = 1;
	
	public TFSClient(String masterIpAddress, int masterPort) {
		this.masterIpAddress = masterIpAddress;
		this.masterPort = masterPort;
	}
	
	/**
	 * Create the directory at a specified path
	 * @param dirName
	 * @param path
	 * @return
	 */
	public int createDir(String dirName, String path) {
		return 0;
	}
	
	/**
	 * Create a file inside a path
	 * @param fileName
	 * @param path
	 * @return
	 */
	public int createFile(String fileName, String path) {
		return 0;
	}
	
	/**
	 * Delete a file providing the filePath
	 * @param filePath
	 * @return
	 */
	public int delFile(String filePath) {
		return 0;
	}
	
	/**
	 * Delete a directory 
	 * @param dirPath directory path in the server side
	 * @param option two options available: ONLY indicates that the directory is deleted only when
	 * the directory is empty, ALL indicates that the directory and all its sub directories and folders are removed
	 * @return
	 */
	public int delDir(String dirPath, int option) {
		return 0;
	}
	
	/**
	 * Get names of all sub-directories of a directory
	 * @param dirPath
	 * @return
	 */
	public String[] getSubDirs(String dirPath) {
		return null;
	}
	
	/**
	 * Get names of all sub-files of a directory
	 * @param dirPath
	 * @return
	 */
	public String[] getSubFiles(String dirPath) {
		return null;
	}
	
	/**
	 * Append a byte array to the end of the file.
	 * @param filePath path of this file in the TFS
	 * @param object object needs to be appended
	 * @return
	 */
	public int append(String filePath, byte[] bytes) {
		return 0;
	}
	
	/**
	 * Read the data from a file at a specific location and length
	 * @param filePath
	 * @param offset
	 * @param dataLength
	 * @return
	 */
	public byte[] read(String filePath, int offset, int dataLength) {
		return null;
	}
	
	/**
	 * Read all the data from a tfs file
	 * @param filePath
	 * @return
	 */
	public byte[] read(String filePath) {
		return null;
	}
	
	/**
	 * Counting the number of replicas for this file
	 * @param filePath path of this file in the TFS
	 * @return
	 */
	public int replicaCount(String filePath) {
		return 0;
	}
}
