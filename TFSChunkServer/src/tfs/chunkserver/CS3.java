package tfs.chunkserver;

import java.io.IOException;

public class CS3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChunkServer chunkServer = new ChunkServer("127.0.0.1", 12231, 11333, "C:\\TFS\\Data\\CS3\\");
		
		try {
			chunkServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
