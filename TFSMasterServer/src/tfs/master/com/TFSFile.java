package tfs.master.com;
import java.util.Date;
import java.util.Random;


public class TFSFile extends TFSNode {
	public TFSFile(String name, TFSDir parent, String absolutePath) {
		super(name, parent, absolutePath);
	}
	
	Date lease;					// storing the primary replica
	String[] chunkServers;		// storing the list of chunkServers	
	
	public String[] getChunkServers() {
		return chunkServers;
	}
	
	public void setChunkServers(String[] servers) {
		this.chunkServers = servers;
	}
	
	public boolean isLeaseValid() {
		if (lease != null)
			return new Date().before(lease) ? true : false;
		else
			return false;
	}
	
	public void setLease(long timespan) {
		long currTime = new Date().getTime();		
		lease = new Date(currTime + timespan);
		
		Random rand = new Random();
		int idx = rand.nextInt(chunkServers.length);
		
		// swap to put the replica address at the top in the list.
		String temp = chunkServers[idx];
		chunkServers[idx] = chunkServers[0];
		chunkServers[0] = temp;
	}
	
	public String getCurrentPrimaryReplica() {
		if (isLeaseValid()) {
			return chunkServers[0];
		}
		
		return "";
	}
}
