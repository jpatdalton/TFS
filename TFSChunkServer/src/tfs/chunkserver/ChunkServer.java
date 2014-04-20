package tfs.chunkserver;

import helpers.LogHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import socket.SocketIO;
import tfs.chunkserver.threads.ChunkWorkerThread;

/**
 * Represents a chunk server
 * @author Nguyen
 *
 */
public class ChunkServer {
	public static String DATA_PATH = "C:\\TFS\\Data\\";
	
	public String masterIpAddress;	// master ip address
	public int masterPort;			// master port
	public int port = 11331;		// default chunkserver port
	
	SocketIO masterSock;			// connection to the master server
	Semaphore semaphore = null;
	
	public static String PING = "ping";
	public static String LEASE_REQUEST = "leaserequest";
	
	public static String GET_FILE_INFO = "fileinfo";
	public static String WRITE_REQUEST = "writerequest";
	public static String PUSH = "push";
	public static String APPEND = "append";
	public static String WRITE = "write";
	public static String READ = "read";
	public static String READALL = "readall";
	public static String COUNT = "count";
	
	public static String STR_OK = "OK";
	public static String STR_SERVER_ERROR = "SERVER_ERROR";
	public static String STR_CLIENT_ERROR = "CLIENT_ERROR";
	public static String STR_NOT_FOUND = "NOT_FOUND";
	public static String STR_INVALID = "INVALID";
	
	public static int OK = 0;
	public static int NOT_FOUND = 1;
	public static int SERVER_ERROR = 2;
	public static int CLIENT_ERROR = 3;
	public static int INVALID = 4;
	
	HashMap<String, TFSChunkFile> files;
	
	public ChunkServer(String masterIpAddress, int masterPort, int port, String dataPath ) {
		this.masterIpAddress = masterIpAddress;
		this.masterPort = masterPort;
		this.port = port;
		this.DATA_PATH = dataPath;
		masterSock = null;
		semaphore = new Semaphore(1, true);
		
		files = new HashMap<String, TFSChunkFile>();
		
		init();
	}

	private void init() {
		try {
			// connect to the master server
			Socket sock = new Socket(masterIpAddress, masterPort);		
			masterSock = new SocketIO(sock);
			
			// load file info
			loadFiles();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private void loadFiles() {
		List<String> logs = LogHelper.getLogs(DATA_PATH + LogHelper.LOG_CHUNKS);
		
		String filePath;
		String[] servers;
		String chunkName;
		TFSChunkFile file;
		for (String log : logs) {
			String[] strs = log.split(";");
			
			switch (strs[0]) {
			case "addfile":
				filePath = strs[1];
				int servercount = Integer.parseInt(strs[2]);
				
				servers = new String[servercount];
				for (int i = 0; i < servercount; i++) {
					servers[i] = strs[3 + i];
				}
				
				file = new TFSChunkFile(filePath);
				file.setChunkServers(servers);
				files.put(filePath, file);
				
				break;
			case "setchunkname":
				filePath = strs[1];
				chunkName = strs[2];
				
				file = files.get(filePath);
				file.setChunkName(chunkName);
				
				break;
			default:
				break;
			}
		}
	}

	public void start() throws IOException {
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(port, 500);
		serverSocket.setSoTimeout(0);
		System.out.println("Chunkserver start at " + port);

		while (true) {
			try {
				Socket sock = serverSocket.accept();
				System.out.println("Accept connection from " + sock.getInetAddress().getHostName());
				SocketIO sockio = new SocketIO(sock);
				
				Thread thread = new Thread(new ChunkWorkerThread(ChunkServer.this, masterSock, sockio, semaphore));
				thread.start();
			} catch (IOException e) {
				
			}
		}
	}
	
	public TFSChunkFile getFileByAbsPath(String filePath) {
		return files.get(filePath);
	}
	
	public void addFile(TFSChunkFile file) {
		files.put(file.getAbsolutePath(), file);
	}
	
	public void setListenPort(int port) {
		this.port = port;
	}
	
	public static void main(String[] args) {
		ChunkServer chunkServer = new ChunkServer("127.0.0.1", 12231, 11331, "C:\\TFS\\Data\\");
		
		try {
			chunkServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
