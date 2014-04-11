
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

		Message msg = new Message(OPERATION.CREATE_DIR, SENDER.CLIENT, absolutePath);
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
	public RETVAL createFile(String filePath, long size) {
		
		Message msg = new Message(OPERATION.CREATE_FILE, SENDER.CLIENT, filePath);
		msg.fileSize = size;
		Write(msg);
		msg = (Message) ReadStream();
		
		msg.printMessage();
		
		return msg.retValue;
	}

	/**
	 * Delete a file providing the filePath
	 * @param filePath
	 * @return
	 */
	public RETVAL delFile(String filePath) {
		Message msg = new Message(OPERATION.DELETE_FILE, SENDER.CLIENT, filePath);
		Write(msg);
		msg = (Message) ReadStream();
		msg.printMessage();
		return msg.retValue;
	}

	/**
	 * Delete a directory and any subdirectories or files contained by the directory
	 * @param dirPath directory path in the server side
	 * @return
	 */

	public RETVAL delDir(String dirPath) {
		Message msg = new Message(OPERATION.DELETE_DIR, SENDER.CLIENT, dirPath);
		Write(msg);

		msg = (Message) ReadStream();

		msg.printMessage();

		return msg.retValue;
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
	public RETVAL append(String filePath, byte[] bytes) {
		try {
			
			filePath = "C:\\CS485\\" + filePath;
			
			File file = new File(filePath);

			//TODO SHOULD RECEIVE INFORMATION FROM SERVER AND BE ADDED TO END		
			//Create file if it doesnt exist
			if(!file.exists()){
				return RETVAL.NOT_FOUND;
			} else {
				
				FileOutputStream output = new FileOutputStream(filePath, true);
				try {
					output.write(bytes);
				} finally {
					output.close();
				}

				return RETVAL.OK;
			}
		} catch (Exception e) {
			return RETVAL.ERROR;
		}	
	}

	/**
	 * Read the data from a file at a specific location and length
	 * @param filePath
	 * @param offset
	 * @param dataLength
	 * @return
	 */
	public byte[] read(String filePath, int offset, int dataLength) {
		byte[] stream = null;
		try {
			RandomAccessFile file = new RandomAccessFile(filePath, "r");
			file.read(stream, offset, dataLength);
			return stream;
		} catch (Exception e) {
			return stream;
		}		
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
