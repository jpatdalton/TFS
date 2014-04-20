package tfs.chunkserver;

import java.io.IOException;

public class CS1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChunkServer chunkServer = new ChunkServer("127.0.0.1", 12231, 11331, "C:\\TFS\\Data\\CS1\\");
		
		try {
			chunkServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
