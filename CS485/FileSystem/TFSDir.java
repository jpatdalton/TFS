
package FileSystem;

import java.util.*;
import Enums.*;


public class TFSDir extends TFSNode
{
	public TFSDir(String name) {
		super(name);
	}
	public ArrayList<TFSDir> subDirs = new ArrayList();
	public ArrayList<TFSFile> subFiles = new ArrayList();
	
	
}
