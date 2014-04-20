package tfs.client;

import helpers.LogHelper;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import socket.*;
import tfs.client.com.TFSClientFile;

/**
 * Client component for the TFS
 * @author Nguyen
 *
 */
public class TFSClient {
	private String masterIpAddress = "127.0.0.1";	// default ip address for master
	private int masterPort = 12231;					// default port for master
	
	public static final int OK = 0;
	public static final int EXISTED = 1;
	public static final int CLIENT_ERROR = 2;
	public static final int SERVER_ERROR = 3;
	public static final int NOT_FOUND = 4;
	public static final int NOT_EMPTY = 5;
	public static final int CONN_ERROR = 6;		// connection error
	public static final int INVALID = 7;
	public static final int REPLICA_EXCEED = 8;
	
	public static final String STR_OK = "OK";
	public static final String STR_EXISTED = "EXISTED";
	public static final String STR_CLIENT_ERROR = "CLIENT_ERROR";
	public static final String STR_SERVER_ERROR = "SERVER_ERROR";
	public static final String STR_NOT_FOUND = "NOT_FOUND";
	public static final String STR_NOT_EMPTY = "NOT_EMPTY";
	public static final String STR_INVALID = "INVALID";
	public static final String STR_REPLICA_EXCEED = "REPLICA_EXCEED";
	
	public static final int DEL_ONLY = 0;
	public static final int DEL_ALL = 1;
	
	public static final String CREATE_DIR = "createdir";
	public static final String CREATE_FILE = "createfile";
	public static final String GET_FILE_INFO = "fileinfo";
	public static final String DELETE_DIR = "deldir";
	public static final String DELETE_FILE = "delfile";
	public static final String GET_DIR_INFO = "dirinfo";
	
	public static final String READ = "read";
	public static final String READALL = "readall";
	public static final String APPEND = "append";
	public static final String PING = "ping";
	public static final String WRITE = "write";
	public static final String WRITE_REQUEST = "writerequest";
	public static final String PUSH = "push";
	public static final String COUNT = "count";
	
	SocketIO masterSock = null;			// holding socket connection to the master
	
	List<TFSClientFile> filesCache;		// storing file information for future reference
	
	public static boolean trace = false;
	
	private String priReplicaIpAddress = null;
	private int priReplicaPort = 0;
	
	/**
	 * Constructor
	 * Initialize a TFS client
	 * @param masterIpAddress
	 * @param masterPort
	 */
	public TFSClient(String masterIpAddress, int masterPort) {
		this.masterIpAddress = masterIpAddress;
		this.masterPort = masterPort;
		this.filesCache = new ArrayList<TFSClientFile>();
		
		init();
	}
	
	private void init() {
		// open connection
		try {
			Socket sock = new Socket(masterIpAddress, masterPort);
			masterSock = new SocketIO(sock);
			
			if (LogHelper.debug) {
				System.out.println("Connected to master (ip=" + masterIpAddress + ", port=" + masterPort + ")");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * Create the directory at a specified path
	 * @param dirName
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public int createDir(String dirName, String path) throws IOException {
		masterSock.write((CREATE_DIR + " " + dirName + " " + path).getBytes());
		masterSock.write("\r\n".getBytes());
		masterSock.flush();
		
		String line = null;
		
		line = masterSock.readLine();
		
		switch(line) {
		case STR_OK:
			return OK;
		case STR_EXISTED:
			return EXISTED;
		case STR_NOT_FOUND:
			return NOT_FOUND;
		case STR_CLIENT_ERROR:
			return CLIENT_ERROR;
		default:
			return SERVER_ERROR;
		}
	}
	
	/**
	 * Create a file inside a path
	 * @param fileName
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public int createFile(String fileName, String path, int numOfReplicas) throws IOException {
		masterSock.write((CREATE_FILE + " " + fileName + " " + path + " " + numOfReplicas).getBytes());
		masterSock.write("\r\n".getBytes());
		masterSock.flush();
		
		String line = null;
		
		line = masterSock.readLine();
		
		switch (line) {			
		case STR_EXISTED:
			return EXISTED;
		case STR_CLIENT_ERROR:
			return CLIENT_ERROR;
		case STR_SERVER_ERROR:
			return SERVER_ERROR;
		case STR_REPLICA_EXCEED:
			return REPLICA_EXCEED;
		default:
			if (line.contains(STR_OK)) {
				String[] strs = line.split(" ");
				
				// set up the file item
				if (trace)
					System.out.println("File chunkservers: ");
				
				TFSClientFile file = new TFSClientFile(path + "/" + fileName);				
				String[] servers = new String[(strs.length - 1) / 2];
				for (int i = 0; i < servers.length; i++) {
					servers[i] = strs[2 * i + 1] + " " + strs[2 * i + 2];
					
					if (trace)
						System.out.println("\t" + servers[i]);
				}				
				file.setChunkServers(servers);
				
				// add to file cache for future reference
				filesCache.add(file);
				
				return OK;
			} else
				return SERVER_ERROR;
		}
	}
	
	/**
	 * Delete a file providing the filePath
	 * @param filePath
	 * @return
	 * @throws IOException 
	 */
	public int delFile(String filePath) throws IOException {
		masterSock.write((DELETE_FILE + " " + filePath).getBytes());
		masterSock.write("\r\n".getBytes());
		masterSock.flush();
		
		String line = masterSock.readLine();
		
		switch (line) {
		case STR_OK:
			return OK;
		case STR_NOT_FOUND:
			return NOT_FOUND;
		default:
			return SERVER_ERROR;
		}
	}
	
	/**
	 * Delete a directory 
	 * @param dirPath directory path in the server side
	 * @param option two options available: ONLY indicates that the directory is deleted only when
	 * the directory is empty, ALL indicates that the directory and all its sub directories and folders are removed
	 * @return
	 * @throws IOException 
	 */
	public int delDir(String dirPath, int option) throws IOException {
		masterSock.write((DELETE_DIR + " " + dirPath + " " + option).getBytes());
		masterSock.write("\r\n".getBytes());
		masterSock.flush();
		
		String line = masterSock.readLine();
		
		switch (line) {
		case STR_OK:
			return OK;
		case STR_NOT_FOUND:
			return NOT_FOUND;
		case STR_NOT_EMPTY:
			return NOT_EMPTY;
		default:
			return SERVER_ERROR;		
		}
	}
	
	/**
	 * Get names of all sub-directories of a directory
	 * @param dirPath
	 * @return
	 * @throws IOException 
	 */
	public String[] getSubDirs(String dirPath) throws IOException {
		masterSock.write((GET_DIR_INFO + " " + dirPath).getBytes());
		masterSock.write("\r\n".getBytes());
		masterSock.flush();
		
		String line = masterSock.readLine();
		String[] subdirs = null;
		
		switch (line) {
		case STR_OK:
			line = masterSock.readLine();	// reversed line for number of subdirs and number of sub files
			int numdirs = Integer.parseInt(line.split(" ")[0]);
			if (numdirs == 0)
				break;
			
			line = masterSock.readLine();	// line for listing sub dirs
			subdirs = line.split(" ");
			break;
		case STR_NOT_FOUND:
			break;
		default:
			break;	
		}
		
		return subdirs;
	}
	
	/**
	 * Get names of all sub-files of a directory
	 * @param dirPath
	 * @return
	 * @throws IOException 
	 */
	public String[] getSubFiles(String dirPath) throws IOException {
		masterSock.write((GET_DIR_INFO + " " + dirPath).getBytes());
		masterSock.write("\r\n".getBytes());
		masterSock.flush();
		
		String line = masterSock.readLine();
		String[] subfiles = null;
		
		switch (line) {
		case STR_OK:
			line = masterSock.readLine();	// reversed line for number of subdirs and number of sub files
			int numfiles = Integer.parseInt(line.split(" ")[1]);
			if (numfiles == 0)
				break;
			
			line = masterSock.readLine();	// line for listing sub dirs
			subfiles = line.split(" ");
			break;
		case STR_NOT_FOUND:
			break;
		default:
			break;	
		}
		
		return subfiles;
	}
	
	public String[][] getDirInfo(String dirPath) throws IOException {
		masterSock.write((GET_DIR_INFO + " " + dirPath).getBytes());
		masterSock.write("\r\n".getBytes());
		masterSock.flush();
		
		String line = masterSock.readLine();
		String[][] infos = new String[2][];
		
		switch (line) {
		case STR_OK:
			line = masterSock.readLine();	// reversed line for number of subdirs and number of sub files
			int numdirs = Integer.parseInt(line.split(" ")[0]);
			int numfiles = Integer.parseInt(line.split(" ")[1]);
			
			line = masterSock.readLine();	// line for listing sub dirs
			if (numdirs != 0) {
				infos[0] = line.split(" ");
			}

			line = masterSock.readLine();	// line for listing sub files
			if (numfiles != 0) {
				infos[1] = line.split(" ");
			}
			
			if (infos[0] != null) {
				for (int i = 0; i < infos[0].length; i++) {
					if (trace)
						System.out.println(infos[0][i] + "/");
				}
			}
			
			if (infos[1] != null) {
				for (int i = 0; i < infos[1].length; i++) {
					if (trace)
						System.out.println(infos[1][i] + "");
				}
			}
			break;
		case STR_NOT_FOUND:
			break;
		default:
			break;	
		}
		
		return infos;		
	}
	
	/**
	 * Append a byte array to the end of the file.
	 * @param filePath path of this file in the TFS
	 * @param object object needs to be appended
	 * @return
	 * @throws IOException 
	 */
	public int append(String filePath, byte[] bytes) throws IOException {
		int ret;
		
		// check in file cache whether such file exists
		TFSClientFile file = getFileFromCache(filePath);
		if (file == null) {		// cannot find the file, try to get file info from the master
			ret = getFileInfo(filePath);
			
			if (ret != OK)
				return NOT_FOUND;
			else {
				file = getFileFromCache(filePath);				
				assert(file != null);
			}
		}
		
		// file info is ready now, get the first chunkserver
		if (file.getChunkServers().length == 0) {
			if (trace)
				System.out.println("There is no chunk servers found.");
			
			return NOT_FOUND;
		}
		
		String strs[] = file.getChunkServers()[0].split(" ");
		
		if (strs.length != 2) {
			if (trace)
				System.out.println("Bad chunk server address.");
			
			return CLIENT_ERROR;
		}
		
		
		String ip = strs[0];
		int port = 0;
		try {
			port = Integer.parseInt(strs[1]);
		} catch (NumberFormatException e) {
			if (trace)
				System.out.println("Bad chunk server address (wrong port).");
			
			return CLIENT_ERROR;
		}
		
		ret = writeRequest(ip, port, file.getAbsolutePath());
		
		if (ret != OK) {
			return ret;
		}
		
		// at this time, the client knows what is the primary replica it should contact with
		// then it can start the write process
		
		// tell the primary replica to append
		Socket sock = new Socket(priReplicaIpAddress, priReplicaPort);
		SocketIO sockIO = new SocketIO(sock);
		
		sockIO.write((TFSClient.APPEND + " " + filePath + " " + bytes.length + "\r\n").getBytes());
		sockIO.write(bytes);
		sockIO.flush();
		
		String line = sockIO.readLine();
		
		sockIO.close();
		
		switch (line) {
		case STR_OK:
			ret = OK;
			break;
		default:
			ret = SERVER_ERROR;
		}		
		
		return ret;
	}

	private int writeRequest(String ipaddress, int port, String filePath) throws UnknownHostException, IOException {
		SocketIO sockIO = new SocketIO(ipaddress, port);
		
		sockIO.write((WRITE_REQUEST + " " + filePath).getBytes());
		sockIO.write("\r\n".getBytes());
		sockIO.flush();
		
		String line = sockIO.readLine();
		
		sockIO.close();
		
		// analyze return message to get the primary address
		priReplicaIpAddress = ipaddress;
		priReplicaPort = port;
		
		if (line.startsWith(STR_OK)) {
			String[] strs = line.split(" ");
			if (strs.length != 3) {
				return SERVER_ERROR;
			}
			
			priReplicaIpAddress = strs[1];
			priReplicaPort = Integer.parseInt(strs[2]);
			
			TFSClientFile file = getFileFromCache(filePath);
			if (file != null) {
				file.setPrimaryReplica(priReplicaIpAddress + " " + priReplicaPort);
			}
			
			return OK;
		} else if (line.startsWith(STR_NOT_FOUND)) {
			return NOT_FOUND;
		} else if (line.startsWith(STR_CLIENT_ERROR)) {
			return CLIENT_ERROR;
		} else {
			return SERVER_ERROR;
		}
	}
	
	/**
	 * Read the data from a file at a specific location and length
	 * @param filePath
	 * @param offset
	 * @param dataLength
	 * @return
	 * @throws IOException 
	 */
	public byte[] read(String filePath, int offset, int dataLength) throws IOException {
		int ret;
		
		TFSClientFile file = getFileFromCache(filePath);
		if (file == null) {		// cannot find the file, try to get file info from the master
			ret = getFileInfo(filePath);
			
			if (ret != OK)
				return null;
			else {
				file = getFileFromCache(filePath);				
				assert(file != null);
			}
		}
		
		// now the file info is ready
		// pick-up a chunkserver to read from
		List<String> servers = new LinkedList<String>();
		for (String server : file.getChunkServers())
			servers.add(server);
		
		Random rand = new Random();
		int index = 0;
		while (!servers.isEmpty()) {
			index = rand.nextInt(servers.size());
			
			String[] strs = servers.get(index).split(" ");
			servers.remove(index);
			
			SocketIO sockIO = new SocketIO(strs[0], Integer.parseInt(strs[1]));
			
			if (dataLength > 0)
				sockIO.write((READ + " " + filePath + " " + offset + " " + dataLength + "\r\n").getBytes());
			else
				sockIO.write((READ + " " + filePath + " " + offset + "\r\n").getBytes());
			
			sockIO.flush();
			
			String line = sockIO.readLine();
			if (line.startsWith(STR_OK)) {
				strs = line.split(" ");
				int length = Integer.parseInt(strs[1]);
				
				byte[] data = new byte[length];
				sockIO.read(data);
				sockIO.close();
				return data;
			} else {
				sockIO.close();
				return null;				
			}
		}
		
		return null;
	}
	
	/**
	 * Read all the data from a tfs file
	 * @param filePath
	 * @return
	 * @throws IOException 
	 */
	public byte[] readall(String filePath) throws IOException {
		int ret;
		
		TFSClientFile file = getFileFromCache(filePath);
		if (file == null) {		// cannot find the file, try to get file info from the master
			ret = getFileInfo(filePath);
			
			if (ret != OK)
				return null;
			else {
				file = getFileFromCache(filePath);				
				assert(file != null);
			}
		}
		
		// now the file info is ready
		// pick-up a chunkserver to read from
		List<String> servers = new LinkedList<String>();
		for (String server : file.getChunkServers())
			servers.add(server);
		
		Random rand = new Random();
		int index = 0;
		while (!servers.isEmpty()) {
			index = rand.nextInt(servers.size());
			
			String[] strs = servers.get(index).split(" ");
			servers.remove(index);
			
			SocketIO sockIO = new SocketIO(strs[0], Integer.parseInt(strs[1]));
			sockIO.write((READALL + " " + filePath + "\r\n").getBytes());
			sockIO.flush();
			
			String line = sockIO.readLine();
			if (line.startsWith(STR_OK)) {
				strs = line.split(" ");
				int length = Integer.parseInt(strs[1]);
				
				byte[] data = new byte[length];
				sockIO.read(data);
				sockIO.close();
				return data;
			} else {
				sockIO.close();
				return null;
			}
		}
		
		return null;
	}
	
	/**
	 * Counting the number of data item for this file
	 * @param filePath path of this file in the TFS
	 * @return
	 * @throws IOException 
	 */
	public int count(String filePath) throws IOException {
		int ret;
		
		TFSClientFile file = getFileFromCache(filePath);
		if (file == null) {		// cannot find the file, try to get file info from the master
			ret = getFileInfo(filePath);
			
			if (ret != OK)
				return -1;
			else {
				file = getFileFromCache(filePath);				
				assert(file != null);
			}
		}
		
		// now the file info is ready
		// pick-up a chunkserver to read from
		List<String> servers = new LinkedList<String>();
		for (String server : file.getChunkServers())
			servers.add(server);
		
		Random rand = new Random();
		int index = 0;
		while (!servers.isEmpty()) {
			index = rand.nextInt(servers.size());
			
			String[] strs = servers.get(index).split(" ");
			servers.remove(index);
			
			SocketIO sockIO = new SocketIO(strs[0], Integer.parseInt(strs[1]));
			sockIO.write((COUNT + " " + filePath + "\r\n").getBytes());
			sockIO.flush();
			
			String line = sockIO.readLine();
			if (line.startsWith(STR_OK)) {
				strs = line.split(" ");
				return Integer.parseInt(strs[1]);
			} else {
				return -1;
			}
		}
		
		return -1;
	}
	
	public int getFileInfo(String filePath) throws IOException {
		masterSock.write((GET_FILE_INFO + " " + filePath).getBytes());
		masterSock.write("\r\n".getBytes());
		masterSock.flush();
		
		String line = masterSock.readLine();
		
		if (line.equals(STR_NOT_FOUND)) {
			return NOT_FOUND;
		} else if (line.startsWith("OK")) {
			String strs[] = line.split(" ");
			
			int serversnum = Integer.parseInt(strs[1]);
			
			if (trace)
				System.out.println("Chunk servers: ");
			
			String[] servers = new String[serversnum];			
			for (int i = 0; i < serversnum; i++) {
				servers[i] = masterSock.readLine();
				
				if (trace)
					System.out.println("\t" + servers[i]);				
			}
			
			TFSClientFile file = new TFSClientFile(filePath);
			file.setChunkServers(servers);
			
			TFSClientFile oldFile = getFileFromCache(filePath);
			if (oldFile != null)
				filesCache.remove(oldFile);
			
			filesCache.add(file);
			
			return OK;
		} else {
			return SERVER_ERROR;
		}		
	}
	
	private TFSClientFile getFileFromCache(String absolutePath) {
		for (TFSClientFile f : filesCache) {
			if (f.getAbsolutePath().equals(absolutePath))
				return f;
		}
		
		return null;
	}

	public void closeSockets() throws IOException {
		if (masterSock != null)
			masterSock.close();
		
//		for (SocketIO chunkSock : chunkSocks)
//			chunkSock.close();
	}
}
