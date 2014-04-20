package tfs.master.threads;

import helpers.LogHelper;
import helpers.PathHelper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import socket.SocketIO;
import tfs.master.MasterServer;
import tfs.master.com.*;

public class MasterWorkerThread implements Runnable {
	private SocketIO sockIO = null;
	private MasterServer masterServer;
	private Semaphore semaphore;
	
	public static final int OK = 0;
	public static final int NOT_FOUND = 1;
	public static final int EXISTED = 2;
	public static final int CLIENT_ERROR = 3;
	public static final int SERVER_ERROR = 4;
	public static final int NOT_EMPTY = 5;
	public static final int INVALID = 6;
	public static final int REPLICA_EXCEED = 7;
	
	public static final long LEASE_EXP_TIME = 60000;
	
	public static final String CREATE_DIR = "createdir";
	public static final String CREATE_FILE = "createfile";
	public static final String GET_FILE_INFO = "fileinfo";
	public static final String GET_DIR_INFO = "dirinfo";
	public static final String DELETE_DIR = "deldir";
	public static final String DELETE_FILE = "delfile";
	public static final String READ = "read";
	public static final String APPEND = "append";
	public static final String PING = "ping";
	public static final String LEASE_REQUEST = "leaserequest";
	
	public static final int DEL_ONLY = 0;
	public static final int DEL_ALL = 1;
	
	int ret;
	
	public MasterWorkerThread(MasterServer masterServer, SocketIO sockIO, Semaphore semaphore) {
		this.masterServer = masterServer;
		this.sockIO = sockIO;
		this.semaphore = semaphore;
		this.ret = OK;
	}

	@Override
	public void run() {
		try {
			while (true) {
				// read a line from the request
				String request = sockIO.readLine();
				String[] strs = request.split(" ");
				
				if (strs.length == 0) {
					ret = CLIENT_ERROR;
					reply(ret);
					return;
				}
				
				TFSFile file;
				TFSDir dir;
				switch (strs[0]) {
				case CREATE_DIR:		// createdir [dir_name] [dir_path]
					if (strs.length != 3) {
						ret = CLIENT_ERROR;
						reply(ret);
						return;
					}
					
					semaphore.acquire();
					createDir(strs[1], strs[2]);
					semaphore.release();
					
					// log record
					if (ret == OK)
						LogHelper.log(request, LogHelper.LOG_RECORD_PATH);
					
					replyCreateDir(ret);
					break;
				case CREATE_FILE:
					if (strs.length < 3) {
						ret = CLIENT_ERROR;
						reply(ret);
						return;
					}
					
					semaphore.acquire();
					if (strs.length == 3) {
						file = createFile(strs[1], strs[2], MasterServer.MAX_REPLICAS);
					} else {					
						file = createFile(strs[1], strs[2], Integer.parseInt(strs[3]));
					}
					semaphore.release();
					
					for (String server: file.getChunkServers()) {
						request += " " + server;
					}
					
					// log record
					if (ret == OK)
						LogHelper.log(request, LogHelper.LOG_RECORD_PATH);
					
					replyCreateFile(ret, file);
					break;
				case DELETE_DIR:
					if (strs.length != 3) {
						ret = CLIENT_ERROR;
						reply(ret);
						return;
					}
					
					int mode;
					
					try {
						mode = Integer.parseInt(strs[2]);
					} catch (Exception e) {
						reply(CLIENT_ERROR);
						return;
					}
					
					semaphore.acquire();
					ret = deleteDir(strs[1], mode);
					semaphore.release();
					
					// log record
					if (ret == OK)
						LogHelper.log(request, LogHelper.LOG_RECORD_PATH);
					
					replyDeleteDir(ret);
					break;
				case DELETE_FILE:
					if (strs.length != 2) {
						ret = CLIENT_ERROR;
						reply(ret);
						return;
					}
					
					semaphore.acquire();
					ret = deleteFile(strs[1]);
					semaphore.release();
					
					// log record
					if (ret == OK)
						LogHelper.log(request, LogHelper.LOG_RECORD_PATH);
					
					replyDeleteFile(ret);
					break;
				case GET_FILE_INFO:		// fileinfo [file_path]
										// reply: OK [file_path] [num_replicas]\r\n[rep1.rep2...]\r\n
					if (strs.length != 2) {
						ret = CLIENT_ERROR;
						reply(ret);
						return;
					}					
					
					file = getFileInfo(strs[1]);						
					replyGetFileInfo(ret, file);
					break;
				case GET_DIR_INFO:		// dirinfo [dir_path]
										// reply OK\r\n[num_sub_dirs][num_sub_files]\r\n[dir1.dir2...]\r\n[file1.file2...]\r\n
					if (strs.length != 2) {
						ret = CLIENT_ERROR;
						reply(ret);
						return;
					}
					
					dir = masterServer.getDir(strs[1]);
					replyGetDirInfo(ret, dir);
					break;
				case LEASE_REQUEST:
					if (strs.length != 2) {
						ret = CLIENT_ERROR;
						reply(ret);
						return;
					}
					
					file = masterServer.getFile(strs[1]);
					
					if (file == null) {
						ret = NOT_FOUND;
						reply(ret);
						return;
					}					
					
					if (!file.isLeaseValid()) {
						file.setLease(LEASE_EXP_TIME);
					}
					
					String server = sockIO.readLine();
//					if (server.equals(file.getCurrentPrimaryReplica())) {
//						ret = OK;
//					} else {
//						ret = INVALID;
//					}
					
					replyLeaseRequest(ret, file);
					break;
				case PING:
					break;
				}
			}
		} catch (IOException | InterruptedException e) {
		} finally {
			try {
				sockIO.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	private void replyLeaseRequest(int ret, TFSFile file) throws IOException {
		switch (ret) {
		case OK:
			sockIO.write(("OK " + LEASE_EXP_TIME).getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case INVALID:
			sockIO.write(("INVALID " + file.getCurrentPrimaryReplica()).getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case NOT_FOUND:
			sockIO.write("NOT_FOUND".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case SERVER_ERROR:
		default:
			sockIO.write("SERVER_ERROR".getBytes());
			sockIO.write("\r\n".getBytes());
			break;			
		}
		
		sockIO.flush();
	}

	private void replyGetDirInfo(int ret, TFSDir dir) throws IOException {
		switch (ret) {
		case NOT_FOUND:
			sockIO.write("NOT_FOUND".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case OK:		
			sockIO.write("OK ".getBytes());
			sockIO.write("\r\n".getBytes());
			
			int dirNum = dir.getSubDirs().size();
			int fileNum = dir.getSubFiles().size();
			sockIO.write((dirNum + " " + fileNum).getBytes());
			sockIO.write("\r\n".getBytes());
			
			if (dirNum > 0) {
				for (int i = 0; i < dirNum - 1; i++) {
					sockIO.write((dir.getSubDirs().get(i).getAbsolutePath() + " ").getBytes());
				}
				sockIO.write((dir.getSubDirs().get(dirNum - 1).getAbsolutePath()).getBytes());
			}
			sockIO.write("\r\n".getBytes());
			
			if (fileNum > 0) {
				for (int i = 0; i < fileNum - 1; i++) {
					sockIO.write((dir.getSubFiles().get(i).getAbsolutePath() + " ").getBytes());
				}
				sockIO.write((dir.getSubFiles().get(fileNum - 1).getAbsolutePath()).getBytes());
			}
			
			sockIO.write("\r\n".getBytes());
			break;
		}
		
		sockIO.flush();
	}

	private void replyGetFileInfo(int ret, TFSFile file) throws IOException {
		switch (ret) {
		case NOT_FOUND:
			sockIO.write("NOT_FOUND".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case OK:
			sockIO.write("OK ".getBytes());
			sockIO.write((file.getChunkServers().length + " ").getBytes());
			sockIO.write("\r\n".getBytes());
			
			for (int i = 0; i < file.getChunkServers().length; i++) {
				sockIO.write(file.getChunkServers()[i].getBytes());
				sockIO.write("\r\n".getBytes());
			}

			break;
		}
		
		sockIO.flush();
	}

	private TFSFile getFileInfo(String filePath) {
		TFSFile file = masterServer.getFile(filePath);
		
		if (file == null) {
			ret = NOT_FOUND;
			return null;
		} else {
			ret = OK;
			return file;
		}
	}

	private TFSDir createDir(String name, String path) {
		TFSDir parent = masterServer.getDir(path);
		
		if (parent == null) {
			ret = CLIENT_ERROR;
			return null;
		}
		
		if (parent.containsDir(name) != null) {
			ret = EXISTED;
			return null;
		}
		
		if (path.equals("/"))
			path = "";
		
		TFSDir dir = new TFSDir(name, parent, path + "/" + name);
		parent.getSubDirs().add(dir);

		ret = OK;
		return dir;
	}	
	
	private void replyCreateDir(int ret) throws IOException {
		switch (ret) {
		case OK:
			sockIO.write("OK".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case EXISTED:
			sockIO.write("EXISTED".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case CLIENT_ERROR:
			sockIO.write("CLIENT_ERROR".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case SERVER_ERROR:
		default:
			sockIO.write("SERVER_ERROR".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		}
		
		sockIO.flush();
	}
	
	private TFSFile createFile(String name, String path, int numOfReplicas) {
		TFSDir parent = masterServer.getDir(path);
		
		if (parent == null) {
			ret = CLIENT_ERROR;
			return null;
		}
		
		if (parent.containsFile(name) != null) {
			ret = EXISTED;
			return null;
		}
		
		// create a new node for file
		if (path.equals("/"))
			path = "";
		TFSFile file = new TFSFile(name, parent, path + "/" + name);
		
		if (numOfReplicas > MasterServer.MAX_REPLICAS) {
			ret = REPLICA_EXCEED;
			return null;
		}
		
		if (numOfReplicas <= 0) {
			numOfReplicas = MasterServer.MAX_REPLICAS;
		}
			
		// set chunkservers which will store chunks of this file
		String[] servers = new String[numOfReplicas];
		
		List<String> serverlist = new LinkedList<String>();
		for (int i = 0 ; i < MasterServer.MAX_REPLICAS; i++) {
			serverlist.add(masterServer.chunkserverList.get(i));
		}		
		
		Random rand = new Random();
		int index = 0;
		int x;
		
		while (index < numOfReplicas) {
			x = rand.nextInt(serverlist.size());
			
			servers[index] = serverlist.get(x);
			serverlist.remove(x);
			
			index++;
		}
		
		file.setChunkServers(servers);
		
		// add the node to the directory tree
		parent.getSubFiles().add(file);
		
		ret = OK;
		return file;
	}
	
	private void replyCreateFile(int ret, TFSFile file) throws IOException {
		switch (ret) {
		case OK:
			assert(file != null);
			
			String[] ips = file.getChunkServers();
			String str = "OK";
			
			for (String ip : ips) {
				str += " " + ip;
			}
			
			sockIO.write(str.getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case EXISTED:
			sockIO.write("EXISTED".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case REPLICA_EXCEED:
			sockIO.write("REPLICA_EXCEED".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case CLIENT_ERROR:
			sockIO.write("CLIENT_ERROR".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case SERVER_ERROR:
		default:
			sockIO.write("SERVER_ERROR".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		}
		
		sockIO.flush();
	}	
	
	private void replyDeleteFile(int ret) throws IOException {
		switch (ret) {
		case NOT_FOUND:
			sockIO.write("NOT_FOUND".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case OK:
			sockIO.write("OK".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		}
		
		sockIO.flush();
	}

	private int deleteFile(String filePath) {
		TFSFile file = masterServer.getFile(filePath);
		if (file == null)
			return NOT_FOUND;
		
		TFSNode node = file.getParent();
		((TFSDir) node).getSubFiles().remove(file);
		
		return OK;
	}

	private int deleteDir(String path, int mode) {
		TFSDir dir = masterServer.getDir(path);
		
		if (dir == null)
			return NOT_FOUND;
		
		if (dir.getSubDirs().size() == 0 && dir.getSubFiles().size() == 0) {
			TFSNode node = dir.getParent();
			((TFSDir)node).getSubDirs().remove(dir);
			
			return OK;
		} else {
			switch (mode) {
			case DEL_ALL:
				TFSNode node = dir.getParent();
				((TFSDir)node).getSubDirs().remove(dir);				
				return OK;
			case DEL_ONLY:
			default:
				return NOT_EMPTY;				
			}
		}
	}
	
	private void replyDeleteDir(int ret) throws IOException {
		switch (ret) {
		case NOT_FOUND:
			sockIO.write("NOT_FOUND".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case OK:
			sockIO.write("OK".getBytes());
			sockIO.write("\r\n".getBytes());			
			break;
		case NOT_EMPTY:
			sockIO.write("NOT_EMPTY".getBytes());
			sockIO.write("\r\n".getBytes());
		}
		
		sockIO.flush();
	}
	
	private void reply(int ret) throws IOException {
		switch (ret) {
		case NOT_FOUND:
			sockIO.write("NOT_FOUND".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case OK:
			sockIO.write("OK".getBytes());
			sockIO.write("\r\n".getBytes());			
			break;
		case NOT_EMPTY:
			sockIO.write("NOT_EMPTY".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case CLIENT_ERROR:
			sockIO.write("CLIENT_ERROR".getBytes());
			sockIO.write("\r\n".getBytes());
			break;
		case SERVER_ERROR:
			sockIO.write("SERVER_ERROR".getBytes());
			sockIO.write("\r\n".getBytes());
			break;			
		default:
			return;
		}
		
		sockIO.flush();		
	}
}
