package tfs.chunkserver;

import java.util.Date;

/**
 * Represents the information of a file that the chunkserver holds
 * @author Nguyen
 *
 */
public class TFSChunkFile {	
	private String absolutePath;
	
	private String[] chunkservers;
	private Date lease;
	
	private String chunkName;		// a unique chunk name for this file
	
	public TFSChunkFile(String absolutePath) {
		this.absolutePath = absolutePath;
		this.chunkName = "";
	}
	
	public void setChunkServers(String[] servers) {
		chunkservers = servers;
	}
	
	public String[] getChunkServers() {
		return chunkservers;
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}
	
	/**
	 * Check whether the lease is still valid
	 * @return
	 */
	public boolean isLeaseValid() {
		if (lease == null)
			return false;
		
		return lease.after(new Date());
	}
	
	/**
	 * Set lease
	 * @param timespan
	 */
	public void setLease(long timespan) {
		long currTime = new Date().getTime();
		
		lease = new Date(currTime + timespan);
	}
	
	public String getChunkName() {
		return chunkName;
	}
	
	public void setChunkName(String chunkName) {
		this.chunkName = chunkName;
	}
	
	/**
	 * Get the current primary replica
	 * @return
	 */
	public String getCurrentPrimaryReplica() {
		return chunkservers[0];
	}
}
