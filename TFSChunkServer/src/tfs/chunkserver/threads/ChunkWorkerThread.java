package tfs.chunkserver.threads;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import socket.SocketIO;
import tfs.chunkserver.ChunkServer;
import tfs.chunkserver.TFSChunkFile;

import helpers.*;

/**
 * A worker thread in the chunkserver is used to serve when a client sends a request to it
 * @author Nguyen
 *
 */
public class ChunkWorkerThread implements Runnable {
	SocketIO masterSock;
	SocketIO sock;
	ChunkServer chunkServer;
	
	Semaphore semaphore;
	
	public ChunkWorkerThread(ChunkServer chunkServer, SocketIO masterSock, SocketIO sock, Semaphore semaphore) {
		this.masterSock = masterSock;
		this.sock = sock;
		this.semaphore = semaphore;
		this.chunkServer = chunkServer;
	}

	@Override
	public void run() {
		try {
			String line = sock.readLine();
			
			if (line == null) {
				replyClientError();
				return;
			}				
			
			String[] strs = line.split(" ");
			if (line.startsWith(ChunkServer.WRITE_REQUEST)) {	// writerequest [file_path]\r\n
				if  (strs.length == 2) {
					handleWriteRequest(strs[1]);
				} else {
					replyClientError();
				}
			} else if (line.startsWith(ChunkServer.APPEND)) {	// append [session_id] [file_path]
				if (strs.length == 3) {
					int len = Integer.parseInt(strs[2]);
					
					byte[] data = new byte[len];
					sock.read(data);
					handleAppend(strs[1], data);
				} else {
					replyClientError();
				}				
			} else if (line.startsWith(ChunkServer.WRITE)) {
				if (strs.length == 4) {
					handleWrite(strs[1], strs[2], Integer.parseInt(strs[3]));					
				} else {
					replyClientError();
				}
			} else if (line.startsWith(ChunkServer.READ) && !line.startsWith(ChunkServer.READALL)) {
				if (strs.length == 3) {
					handleRead(strs[1], Integer.parseInt(strs[2]));
				} else if (strs.length == 4) {
					handleRead(strs[1], Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
				} else {
					replyClientError();
				}
			} else if (line.startsWith(ChunkServer.READALL)) {
				if (strs.length == 2) {
					handleReadAll(strs[1]);
				} else {
					replyClientError();
				}
			} else if (line.startsWith(ChunkServer.COUNT)) {
				if (strs.length == 2) {
					handleCount(strs[1]);
				} else {
					replyClientError();
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void handleCount(String filePath) throws IOException {
		TFSChunkFile file = chunkServer.getFileByAbsPath(filePath);
		
		// cannot find file information, return not found.
		if (file == null || file.getChunkName() == null || file.getChunkName() == "") {
			replyNotFound();
			return;
		}
		
		String localFileName = file.getChunkName();
		byte[] data = SerializationHelper.getBytesFromFile(ChunkServer.DATA_PATH + localFileName);

		// count the item
		int datalen = data.length;
		int offset = 0;
		int count = 0;
		while (offset < datalen) {
			byte[] intval = new byte[4];
			System.arraycopy(data, offset, intval, 0, 4);
			offset += 4 + SerializationHelper.bytesToInt(intval);
			count++;
		}
		
		sock.write(("OK " + count + "\r\n").getBytes());
		sock.write(data);
		sock.flush();	
	}

	private void handleReadAll(String filePath) throws IOException {
		TFSChunkFile file = chunkServer.getFileByAbsPath(filePath);
		
		// cannot find file information, return not found.
		if (file == null || file.getChunkName() == null || file.getChunkName() == "") {
			replyNotFound();
			return;
		}
		
		String localFileName = file.getChunkName();
		byte[] filebytes = SerializationHelper.getBytesFromFile(ChunkServer.DATA_PATH + localFileName);
		
		sock.write(("OK " + filebytes.length + "\r\n").getBytes());
		sock.write(filebytes);
		sock.flush();
	}

	private void handleRead(String filePath, int offset, int length) throws IOException {
		TFSChunkFile file = chunkServer.getFileByAbsPath(filePath);
		
		// cannot find file information, return not found.
		if (file == null || file.getChunkName() == null || file.getChunkName() == "") {
			replyNotFound();
			return;
		}
		
		String localFileName = file.getChunkName();
		byte[] filebytes = SerializationHelper.getBytesFromFile(ChunkServer.DATA_PATH + localFileName);

		byte[] data = new byte[length];
		System.arraycopy(filebytes, offset, data, 0, length);
		
		sock.write(("OK " + data.length + "\r\n").getBytes());
		sock.write(data);
		sock.flush();
	}

	private void handleRead(String filePath, int offset) throws IOException {
		TFSChunkFile file = chunkServer.getFileByAbsPath(filePath);
		
		// cannot find file information, return not found.
		if (file == null || file.getChunkName() == null || file.getChunkName() == "") {
			replyNotFound();
			return;
		}
		
		String localFileName = file.getChunkName();
		byte[] filebytes = SerializationHelper.getBytesFromFile(ChunkServer.DATA_PATH + localFileName);

		int datalen = SerializationHelper.bytesToInt(filebytes);
		
		byte[] data = new byte[datalen];
		System.arraycopy(filebytes, offset + 4, data, 0, datalen);
		
		sock.write(("OK " + data.length + "\r\n").getBytes());
		sock.write(data);
		sock.flush();
	}

	private void handleWrite(String sessionId, String filePath, int offset) {
		//TODO
	}

	private void handleAppend(String filePath, byte[] data) throws IOException {		
		TFSChunkFile file = chunkServer.getFileByAbsPath(filePath);
		if (file == null) {
			file = getFileInfoFromMaster(filePath);
			
			if (file == null)
				return;
		}
		
		// append the data to the chunk
		String chunkname = file.getChunkName();
		if (chunkname == null || chunkname == "") {
			// generate a unique chunk name for this file
			chunkname = UUID.randomUUID().toString();
			file.setChunkName(chunkname);
			
			String log = "setchunkname";
			log += ";" + filePath;
			log += ";" + chunkname;
			LogHelper.log(log, ChunkServer.DATA_PATH + LogHelper.LOG_CHUNKS);
		}
			
		byte[] tfsdata = new byte[data.length + 4];
		byte[] intBytes = SerializationHelper.intToBytes(data.length);
		System.arraycopy(intBytes, 0, tfsdata, 0, 4);
		System.arraycopy(data, 0, tfsdata, 4, data.length);
		
		SerializationHelper.writeBytesToFile(ChunkServer.DATA_PATH + chunkname, tfsdata);
		
		// call other replicas to append
		if (file.isLeaseValid()) {
			String primaryReplica = sock.getLocalAddress().getHostAddress() + " " + sock.getLocalPort();			
			boolean success = true;
			
			for (int i = 0; i< file.getChunkServers().length; i++) {
				if (!file.getChunkServers()[i].equals(primaryReplica)) {
					String[] strs = file.getChunkServers()[i].split(" ");
					String ip = strs[0];
					int port = Integer.parseInt(strs[1]);
					
					Socket sock = new Socket(ip, port);
					SocketIO sockIO = new SocketIO(sock);
					
					sockIO.write((ChunkServer.APPEND + " " + filePath + " " + data.length).getBytes());
					sockIO.write("\r\n".getBytes());
					sockIO.write(data);
					sockIO.flush();
					
					String line = sockIO.readLine();
					
					sockIO.close();
					
					if (line.startsWith(ChunkServer.STR_OK)) {
						continue;
					} else {
						success = false;
						break;
					}
				}
			}			
			
			if (success) {
				replyOk();
			} else {
				replyServerError();
			}
		} else
			replyOk();
	}

	private void handleWriteRequest(String filePath) throws IOException {		
		// try to get information of the file in the memory 
		TFSChunkFile file = chunkServer.getFileByAbsPath(filePath);
		
		// cannot find the file in the local cache, request for data info in the master
		if (file == null) {
			file = getFileInfoFromMaster(filePath);
			
			if (file == null)
				return;
		}
		
		if (!file.isLeaseValid()) {
			masterSock.write((ChunkServer.LEASE_REQUEST + " " + filePath).getBytes());
			masterSock.write("\r\n".getBytes());
			masterSock.write((sock.getLocalAddress().getHostAddress() + " " + sock.getLocalPort()).getBytes());
			masterSock.write("\r\n".getBytes());
			masterSock.flush();
			
			String line = masterSock.readLine();
			String[] strs = line.split(" ");
			String server = "";
			if (line.startsWith(ChunkServer.STR_OK) || line.startsWith(ChunkServer.STR_INVALID)) {
				if (line.startsWith(ChunkServer.STR_OK)) {
					long timespan = Long.parseLong(strs[1]);					
					file.setLease(timespan);					
					server = sock.getLocalAddress().getHostAddress() + " " + sock.getLocalPort();
				} else if (line.startsWith(ChunkServer.STR_INVALID)) {
					server = strs[1] + " " + strs[2];
				}
				
				sock.write((ChunkServer.STR_OK + " " + server).getBytes());
				sock.write("\r\n".getBytes());
				sock.flush();
			} else if (line.startsWith(ChunkServer.STR_NOT_FOUND)){
				replyNotFound();
			} else {
				replyServerError();
			}
		} else {
			sock.write((ChunkServer.STR_OK + " " + file.getCurrentPrimaryReplica() + "\r\n").getBytes());
			sock.flush();
		}
	}	
	
	private TFSChunkFile getFileInfoFromMaster(String filePath) throws IOException {
		masterSock.write((ChunkServer.GET_FILE_INFO + " " + filePath).getBytes());
		masterSock.write("\r\n".getBytes());
		masterSock.flush();
		
		String line = masterSock.readLine();
		
		if (line.startsWith(ChunkServer.STR_OK)) {
			String[] strs = line.split(" ");
			int serversnum = Integer.parseInt(strs[1]);
			
			String[] servers = new String[serversnum];
			
			for (int i = 0; i < serversnum; i++)
				servers[i] = masterSock.readLine();
			
			TFSChunkFile file = new TFSChunkFile(filePath);
			file.setChunkServers(servers);
			chunkServer.addFile(file);		
			
			String log = "addfile";
			log += ";" + file.getAbsolutePath();
			log += ";" + file.getChunkServers().length;
			for (int i = 0; i < file.getChunkServers().length; i++) {
				log += ";" + file.getChunkServers()[i];
			}
			
			LogHelper.log(log, ChunkServer.DATA_PATH + LogHelper.LOG_CHUNKS);
			
			return file;
		} else if (line.equals(ChunkServer.NOT_FOUND)) {
			replyNotFound();
			return null;
		} else {
			replyServerError();
			return null;
		}		
	}

	private void replyClientError() throws IOException {
		sock.write(ChunkServer.STR_CLIENT_ERROR.getBytes());
		sock.write("\r\n".getBytes());
		sock.flush();
	}
	
	private void replyServerError() throws IOException {
		sock.write(ChunkServer.STR_SERVER_ERROR.getBytes());
		sock.write("\r\n".getBytes());
		sock.flush();
	}
	
	private void replyNotFound() throws IOException {
		sock.write(ChunkServer.STR_NOT_FOUND.getBytes());
		sock.write("\r\n".getBytes());
		sock.flush();
	}
	
	private void replyOk() throws IOException {
		sock.write(ChunkServer.STR_OK.getBytes());
		sock.write("\r\n".getBytes());
		sock.flush();
	}
}
