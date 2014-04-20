package tfs.master;

public class MasterOperation {
	public static final int OK = 0;
	public static final int NOT_FOUND = 1;
	public static final int EXISTED = 2;
	public static final int CLIENT_ERROR = 3;
	public static final int SERVER_ERROR = 4;
	public static final int NOT_EMPTY = 5;
	

	
	public static final String CREATE_DIR = "createdir";
	public static final String CREATE_FILE = "createfile";
	public static final String GET_FILE_INFO = "fileinfo";
	public static final String GET_DIR_INFO = "dirinfo";
	public static final String DELETE_DIR = "deldir";
	public static final String DELETE_FILE = "delfile";
	public static final String READ = "read";
	public static final String APPEND = "append";
	public static final String PING = "ping";
	public static final String LEASE_REQUEST = "leaserequest";
	
	public static final int DEL_ONLY = 0;
	public static final int DEL_ALL = 1;
	
	
}
