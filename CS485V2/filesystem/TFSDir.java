
package filesystem;

import java.util.*;


public class TFSDir extends TFSNode
{
	public TFSDir(String name, TFSNode parent) {
		super(name, parent);
	}
	public ArrayList<TFSDir> subDirs = new ArrayList();
	public ArrayList<TFSFile> subFiles = new ArrayList();
	
}
