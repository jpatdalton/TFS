
package FileSystem;

public class TFSNode 
{
	
	public TFSNode(String name, TFSNode parent){
		this.name = name;
		this.parent = parent;
	}
	
	//parent of this node
	TFSNode parent;
	//Directory/file name
	public String name;
	//Absolute path of this mode
	String absolutePath;
}


