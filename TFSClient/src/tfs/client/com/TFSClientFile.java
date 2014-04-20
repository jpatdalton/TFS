package tfs.client.com;

/**
 * Represents a file information that a client holds
 * @author Nguyen
 *
 */
public class TFSClientFile {
	String absolutePath;		// absolute path of the file
	
	String[] chunkServers;		// list of chunk servers
	
	public TFSClientFile(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	
	public void setChunkServers(String[] chunkServers) {
		this.chunkServers = chunkServers;
	}
	
	public String[] getChunkServers() {
		return chunkServers;
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setPrimaryReplica(String address) {
		int index = -1;
		
		// find the existing replica with provided address
		for (int i = 0; i < chunkServers.length; i++) {
			if (chunkServers[i].equals(address)) {
				index = i;
				break;
			}
		}
		
		// if the address does not exist in the chunkservers list, add the address to the list
		if (index == -1) {
			String[] newChunkServers = new String[chunkServers.length + 1];
			newChunkServers[0] = address;
			System.arraycopy(chunkServers, 0, newChunkServers, 1, chunkServers.length);
		} else {
			// swap the primary replica to the top of the list
			String temp = chunkServers[index];
			chunkServers[index] = chunkServers[0];
			chunkServers[0] = temp;
		}
	}
}
