package tfs.chunkserver.threads;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import socket.SocketIO;
import tfs.chunkserver.ChunkServer;

public class PingThread implements Runnable {
	SocketIO masterSock;
	Semaphore semaphore;
	
	public static final int PING_TIME_OUT = 300;
	
	int pTime;
	
	public PingThread(SocketIO masterSock, Semaphore semaphore) {
		this.masterSock = masterSock;
		this.semaphore = semaphore;
		
		pTime = 0;
	}

	@Override
	public void run() {
		String line = "";
		while (true) {
			try {
				semaphore.acquire();
			
				masterSock.write(ChunkServer.PING.getBytes());
				masterSock.write("\r\n".getBytes());
				masterSock.flush();
				
				line = masterSock.readLine();
				
				if (line != null && line.equals("OK")) {
					pTime = 0;
				} else {
					System.out.println("Fail to connect with the master");
					pTime += 60;
				}				
				
				semaphore.release();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}			
		}
	}

}
