
package MainApplications;

import java.io.*;
import java.net.*;
import java.util.Random;

import Threads.*;
import Message.*;
import Enums.*;

public class TFSClient extends Client {

	public static void main(String[] args) {

		TFSClient client = new TFSClient();
	}
	
	String ip;
	Socket clientSocket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;

	public TFSClient(){
		super();
		ip = getIP();
	}

	public long getNewID(){
		Random r = new Random();
		long ret = r.nextLong();
		return ret;
	}
	
	
	/**
	 * Create the directory at a specified path
	 * @param dirName
	 * @param path
	 * @return
	 */
	public RETVAL createDir(String absolutePath) {
		
		System.out.println("HERE");
		
		Message msg = new Message(OPERATION.CREATE_DIR, SENDER.CLIENT, ip, absolutePath);
		Write(msg);
		
		msg = (Message) ReadStream();
		
		
		
		msg.printMessage();
		
		return msg.retValue;
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
	 * Delete a directory and any subdirectories or files contained by the directory
	 * @param dirPath directory path in the server side
	 * @return
	 */
	public RETVAL delDir(String dirPath) {
		File dir = new File(dirPath);
	    if (!dir.exists()) return RETVAL.NOT_FOUND;
	    if (dir.isDirectory()) {
	    	for (File f : dir.listFiles()) delDir(f.getPath());
	    		dir.delete();
	    } else {
	       dir.delete();
	    } 
		return RETVAL.NOT_FOUND;
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
	
	public void ReadFile(String absolutePath){
		
		
		
		
	}
	
	public void Append(String absolutePath){
		
		
		
		
		
		
	}

	public String getIP(){
		
		String ip = null;
		try{
			ip = Inet4Address.getLocalHost().getHostAddress();
		}
		catch(UnknownHostException e){
			System.out.println(e.getMessage());
		}
		
		return ip;
	}
	
	public void DecipherMsg(Message msg) {
		
		
		
	}
}
