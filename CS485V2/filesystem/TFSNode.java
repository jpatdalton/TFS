
package filesystem;

public class TFSNode 
{
	
	public TFSNode(String name, TFSNode parent){
		this.name = name;
		this.parent = parent;
		
		
		if(parent != null && parent.absolutePath == "")
			absolutePath = name;
		else if(parent != null)
			absolutePath = parent.absolutePath + "\\" + name;
		else
			absolutePath = "";
	}
	
	//parent of this node
	public TFSNode parent;
	//Directory/file name
	public String name;
	//Absolute path of this mode
	public String absolutePath;
}


