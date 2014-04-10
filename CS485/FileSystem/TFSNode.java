
package FileSystem;

public class TFSNode 
{
	
	public TFSNode(String name){
		this.name = name;
	}
	
	//parent of this node
	TFSNode parent;
	//Directory/file name
	String name;
	//Absolute path of this mode
	String absolutePath;
}


