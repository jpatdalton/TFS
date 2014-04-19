
package filesystem;

import java.util.*;

public class TFSFile extends TFSNode {
   	
	Date lease;                   	// storing the lease
   	ArrayList<String> chunkServers;        	// storing the list of chunkServers
                                     	// the first chunkserver is the primary replica
    ArrayList<String> chunkIds;
    // storing the list of chunkIds created
    
    String path;
    
    public TFSFile(String name, TFSDir parent){
    	super(name, parent);
    	
 
	}
    
}