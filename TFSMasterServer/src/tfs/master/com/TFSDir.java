package tfs.master.com;
import java.util.LinkedList;
import java.util.List;

public class TFSDir extends TFSNode {	
	public TFSDir(String name, TFSDir parent, String absolutePath) {
		super(name, parent, absolutePath);
		
		subDirs = new LinkedList<TFSDir>();
		subFiles = new LinkedList<TFSFile>();
	}

	List<TFSDir> subDirs;	// sub-directories
	List<TFSFile> subFiles;	// sub-files	
	
	/**
	 * Return all children of this node
	 * @return
	 */
	public List<TFSNode> getChildren() {
		List<TFSNode> nodeList = new LinkedList<TFSNode>();
		
		for (TFSDir dir : subDirs)
			nodeList.add(dir);
		
		for (TFSFile file : subFiles)
			nodeList.add(file);
		
		return nodeList;
	}
	
	public List<TFSDir> getSubDirs() {
		return subDirs;
	}
	
	public List<TFSFile> getSubFiles() {
		return subFiles;
	}
	
	public TFSDir containsDir(String dirName) {
		for (TFSDir dir : subDirs)
			if (dir.getName().equals(dirName))
				return dir;
		
		return null;
	}
	
	public TFSFile containsFile(String fileName) {
		for (TFSFile file : subFiles)
			if (file.getName().equals(fileName))
				return file;
		
		return null;		
	}
}