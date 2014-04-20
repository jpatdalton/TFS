package tfs.master.com;
public class TFSNode {
	TFSDir parent;			// parent of this node
	String name;			// directory/file name
	
	String absolutePath;	// absolute path of this node
	
	public TFSNode(String name, TFSDir parent, String absolutePath) {
		this.name = name;
		this.parent = parent;
		
		this.absolutePath = absolutePath;
	}
	
	/**
	 * Gets node name
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public TFSNode getParent() {
		return parent;
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}
}
