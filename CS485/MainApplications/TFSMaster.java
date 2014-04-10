package MainApplications;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

import Message.*;
import Enums.*;
import FileSystem.*;
import Threads.*;

//This is the server, where all the clients connect to.

public class TFSMaster implements Runnable {

	ArrayList<ServerClient> clients = new ArrayList();

	ServerSocket ss = null;

	ObjectOutputStream oos; 
	ObjectInputStream ois;

	public TFSDir root;

	public TFSMaster(){

		CreateRoot();

		//accept 6 clients.
		SetupConnections();


	}	

	public void CreateRoot(){

		File theDir = new File("C:\\CS485");
		boolean result = theDir.mkdir();

		// TODO CLEAN OUT ROOT PER EACH RUN		
		root = new TFSDir("C:\\CS485");

	}

	//setup up new client, and put them into ArrayList stuff can be written and read from all.
	public void SetupThreads(Socket socket){
		clients.add(new ServerClient(socket, this));	
	}

	//SetupConnections.
	public void SetupConnections(){
		try {
			ss = new ServerSocket(3434);

			System.out.println("Waiting for Connection");
			Socket socket = ss.accept();

			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());


			/* Message msg = (Message) ois.readObject();
			msg.printMessage();
			oos.writeObject(msg);

			System.out.println("Set up Socket"); */
			//SetupThreads(socket);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}	

		Thread t = new Thread(this);
		t.start();

	}

	public void WriteObject(Object obj){
		try {
			oos.writeObject(obj);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object ReadObject(){
		try {
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private RETVAL delete(String path) {
		File dir = new File(path);
	    if (!dir.exists()) return RETVAL.NOT_FOUND;
	    // Delete if dirPath is a directory
	    if (dir.isDirectory()) {
	    	for (File f : dir.listFiles()) delete(f.getPath());
	    	dir.delete();
	    	
	    	//Delete the dir from the TFSDir root
	    	String[] split = path.split(":?////");
	    	TFSDir current = root;
	    	for (int i = 0; i < split.length-1; i++) {
	    		for (int j = 0; j < current.subDirs.size(); j++) {
	    			if (current.subDirs.get(j).name.equals(split[i]))
	    				current = current.subDirs.get(j);
	    		}
	    	}
	    	for (int i = 0; i < current.subDirs.size(); i++) {
	    		if (current.subDirs.get(i).name.equals(split[split.length-1]))
	    			current.subDirs.remove(i);
	    	}
	    	return RETVAL.OK;
	    
	    // Delete if dirPath is a file
	    } else {
	       dir.delete();
	       //Delete the file from the TFSDir root
	       String[] split = path.split(":?////");
	       TFSDir current = root;
	       for (int i = 0; i < split.length-1; i++) {
	    		for (int j = 0; j < current.subDirs.size(); j++) {
	    			if (current.subDirs.get(j).name.equals(split[i]))
	    				current = current.subDirs.get(j);
	    		}
	    	}
	    	for (int i = 0; i < current.subFiles.size(); i++) {
	    		if (current.subFiles.get(i).name.equals(split[split.length-1]))
	    			current.subFiles.remove(i);
	    	}
	    	return RETVAL.OK;
	    } 
	}

	//main
	public static void main(String[] args) {
		TFSMaster master = new TFSMaster();

	}

	@Override
	public void run() {

		while(true){

			Message msg = (Message) ReadObject();

			WriteObject(msg);

		}
	}

}
