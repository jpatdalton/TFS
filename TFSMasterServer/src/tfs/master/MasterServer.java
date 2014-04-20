package tfs.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Semaphore;

import helpers.*;
import socket.*;
import tfs.master.com.*; 
import tfs.master.threads.MasterWorkerThread;

public class MasterServer {
	public TFSDir root = null;
	
	public static int MAX_REPLICAS;
	
	public Semaphore semaphore;
	
	public List<String> chunkserverList;
	
	SocketIO sockIO;
	int ret;
	
	public MasterServer() {
		// get settings
		MAX_REPLICAS = Integer.parseInt(ConfigHelper.getValue("maxreplicas"));
		
		// create root directory
		root = new TFSDir("root", null, "");
		
		// get list of chunkservers
		chunkserverList = ConfigHelper.getChunkServers();
		
		semaphore = new Semaphore(1, true);
		
		// load directory structure from log records
		loadDirectories();
	}
	
	private void loadDirectories() {
		List<String> logs = LogHelper.getLogs(LogHelper.LOG_RECORD_PATH);
		
		String[] strs;
		for (String log : logs) {
			strs = log.split(" ");
			
			switch (strs[0]) {
			case MasterWorkerThread.CREATE_DIR:
				createDirFromLog(strs[1], strs[2]);
				break;
			case MasterWorkerThread.CREATE_FILE:
				String[] servers = new String[(strs.length - 4) / 2];
				for (int i = 0; i < (strs.length - 4) / 2; i++) {
					servers[i] = strs[2 * i + 4] + " " + strs[2 * i + 5];
				}
				
				createFileFromLog(strs[1], strs[2], servers);
				break;
			case MasterWorkerThread.DELETE_DIR:
				deleteDirFromLog(strs[1], Integer.parseInt(strs[2]));
				break;
			case MasterWorkerThread.DELETE_FILE:
				deleteFileFromLog(strs[1]);
				break;
			}
		}
	}
	
	private TFSDir createDirFromLog(String name, String path) {
		TFSDir parent = getDir(path);
		
		if (parent == null) {
			return null;
		}
		
		if (parent.containsDir(name) != null) {
			return null;
		}
		
		if (path.equals("/"))
			path = "";
		
		TFSDir dir = new TFSDir(name, parent, path + "/" + name);
		parent.getSubDirs().add(dir);

		return dir;
	}	
	
	private TFSFile createFileFromLog(String name, String path, String[] servers) {
		TFSDir parent = getDir(path);
		
		if (parent == null) {
			return null;
		}
		
		if (parent.containsFile(name) != null) {
			return null;
		}
		
		// create a new node for file
		if (path.equals("/"))
			path = "";
		TFSFile file = new TFSFile(name, parent, path + "/" + name);
		
		// set chunkservers which will store chunks of this file
		file.setChunkServers(servers);
		
		// add the node to the directory tree
		parent.getSubFiles().add(file);

		return file;
	}
	
	private int deleteDirFromLog(String path, int mode) {
		TFSDir dir = getDir(path);
		
		if (dir == null)
			return -1;
		
		if (dir.getSubDirs().size() == 0 && dir.getSubFiles().size() == 0) {
			TFSNode node = dir.getParent();
			((TFSDir)node).getSubDirs().remove(dir);
			
			return 0;
		} else {
			switch (mode) {
			case MasterWorkerThread.DEL_ALL:
				TFSNode node = dir.getParent();
				((TFSDir)node).getSubDirs().remove(dir);				
				return 0;
			case MasterWorkerThread.DEL_ONLY:
			default:
				return -1;				
			}
		}
	}
	
	private int deleteFileFromLog(String filePath) {
		TFSFile file = getFile(filePath);
		if (file == null)
			return -1;
		
		TFSNode node = file.getParent();
		((TFSDir) node).getSubFiles().remove(file);
		
		return 0;
	}
	
	/**
	 * Start the master server
	 * @throws IOException 
	 */
	public void start() throws IOException {
		// create a server socket
		ServerSocket providerSocket = new ServerSocket(12231, 500);
		providerSocket.setSoTimeout(0);
		System.out.println("Master server started. Open connection at port 12231");
		
		// repeatedly listen for requests
		while (true) {
			try {
				// wait for connection
				Socket socket = providerSocket.accept();
				
				if (LogHelper.debug) {
					System.out.println("Connection received from " + socket.getInetAddress().getHostName());
					System.out.println(socket.getLocalAddress().getHostAddress() + " " + socket.getLocalPort());
					System.out.println(socket.getInetAddress().getHostAddress() + " " + socket.getPort());
				}
	
				sockIO = new SocketIO(socket);
				
				Thread thread = new Thread(new MasterWorkerThread(MasterServer.this, sockIO, semaphore));
				thread.start();
				
				System.out.println("Start new thread.");
				
			} catch (IOException e) {
				
			}
		}
	}
	
	// checks whether a directory exists
	// return the directory if exists, otherwise return null
	public TFSDir getDir(String path) {
		String[] strNodes = PathHelper.getNodes(path);
		
		TFSDir node = root;
		
		for (int i = 1; i < strNodes.length; i++) {
			node = node.containsDir(strNodes[i]);
			
			if (node == null)
				return null;
		}
		
		return node;
	}
	
	// check whether a file exists
	// return the file if exists, otherwise return null
	public TFSFile getFile(String path) {
		String[] strs = path.split("/");
		
		String fileName = strs[strs.length - 1];
		String dirPath = path.substring(0, path.length() - fileName.length() - 1);
		if (dirPath.equals(""))
			dirPath = "/";
		
		TFSDir dir = getDir(dirPath);
		if (dir != null) {
			return dir.containsFile(fileName);
		} else {
			return null;
		}
	}

	public static void main(String[] args) {			
		LogHelper.debug = true;
		MasterServer masterServer = new MasterServer();
		
		try {
			masterServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
