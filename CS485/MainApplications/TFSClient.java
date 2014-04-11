
package MainApplications;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import Threads.*;
import Message.*;
import Enums.*;
import Helpers.*;

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

	public RETVAL createFileLocally(String filePath){

		File file = new File(filePath);
		if(file.exists())
			return RETVAL.EXISTS;

		try {
			file.createNewFile();
			return RETVAL.OK;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RETVAL.CLIENT_ERROR;
		}

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
	 * Read the data from a file at a specific location and length
	 * @param filePath
	 * @param offset
	 * @param dataLength
	 * @return
	 */
	public Message read(String filePath, int offset, int dataLength) {

		filePath = "C:\\CS485\\" + filePath;

		Message msg = new Message(OPERATION.READ_FILE, SENDER.CHUNK_SERVER, filePath);

		byte[] stream = null;
		try {
			RandomAccessFile file = new RandomAccessFile(filePath, "r");
			file.read(stream, offset, dataLength);

			msg.bytes = stream;
			msg.retValue = RETVAL.OK;
			return msg;
		} catch (Exception e) {
			msg.bytes = null;
			msg.retValue = RETVAL.NOT_FOUND;
			return msg;
		}		
	}

	public boolean checkFileSystemLocally(String filePath){

		File file = new File(filePath);	
		return file.exists();

	}

	public RETVAL writeFileLocally(String filePath, byte bytes[]){

		try {

			System.out.println("APPEND: " + filePath);

			//filePath = "C:\\CS485\\" + filePath;

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

	public Message readFileLocally(String filePath){

		byte[] data = SerializationHelper.getBytesFromFile(filePath);

		Message msg = new Message(OPERATION.READ_FILE, SENDER.CHUNK_SERVER, filePath);

		if(data == null)
			msg.retValue = RETVAL.NOT_FOUND;
		else
			msg.retValue = RETVAL.OK;

		msg.bytes = data;


		return msg;

	}




	/**
	 * Append a byte array to the end of the file.
	 * @param filePath path of this file in the TFS
	 * @param object object needs to be appended
	 * @return
	 */
	public RETVAL append(String filePath, byte[] bytes) {
		try {

			System.out.println("APPEND: " + filePath);

			filePath = "C:\\CS485\\" + filePath;

			File file = new File(filePath);

			int length = bytes.length;

			int fileLength = (int)file.length();

			byte header [] = ByteBuffer.allocate(4).putInt(length).array();

			//TODO SHOULD RECEIVE INFORMATION FROM SERVER AND BE ADDED TO END		
			//Create file if it doesnt exist
			if(!file.exists()){
				return RETVAL.NOT_FOUND;
			} else {

				FileOutputStream output = new FileOutputStream(filePath, true);
				try {

					output.write(header); //TODO
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
	 * Read all the data from a tfs file
	 * @param filePath
	 * @return
	 */
	public Message read(String filePath) {

		filePath = "C:\\CS485\\" + filePath;

		Message msg = new Message(OPERATION.READ_FILE, SENDER.CHUNK_SERVER, filePath);
		msg.printMessage();

		byte[] stream = SerializationHelper.getBytesFromFileWithOffset(filePath); //TODO

		//byte[] stream = SerializationHelper.getBytesFromFile(filePath);

		msg.bytes = stream;

		if(stream == null)
		{
			msg.retValue = RETVAL.NOT_FOUND;	
		}
		else
		{
			msg.retValue = RETVAL.OK;
		}

		return msg;

	}

	public int getNumberOfHaystackFiles(String absolutePath){


		absolutePath = "C:\\CS485\\" + absolutePath;
		File file = new File(absolutePath);

		if(!file.exists())
			return -1;


		byte[] data = null;

		byte header [] = new byte [4];

		int offset = 0;
		int fileCounter = 0;
		
		try {

			FileInputStream input = new FileInputStream(file);
			while(offset < file.length()){

				input.read(header);
				offset += 4;
				ByteBuffer bb = ByteBuffer.wrap(header);
				int dataSize = bb.getInt();
				data = new byte[dataSize];
				input.read(data);
				offset += dataSize;
				fileCounter++;
			}

			input.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileCounter;
		
	}


	public ArrayList<String> getSubdirectories(String absolutePath){

		File file = new File(absolutePath);
		ArrayList<String> subdirectories = new ArrayList();

		if(file.exists() && file.isDirectory()){
			File [] names = file.listFiles();
			for(File f: names){
				if(f.isDirectory())
					subdirectories.add(f.getAbsolutePath());
			}
		}

		return subdirectories;

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
