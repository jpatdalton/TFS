package Threads;

import java.net.*;
import java.io.*;
import java.net.Socket;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import MainApplications.*;
import Message.*;
import Enums.*;
import FileSystem.*;

public class ServerClient implements Runnable {

	
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;
	
	TFSMaster master;
	
	public ServerClient(Socket s, TFSMaster master)
	{
		try{
			ois = new ObjectInputStream(s.getInputStream());
			
			Message msg = (Message) ReadStream();
			msg.printMessage();
			
			oos = new ObjectOutputStream(s.getOutputStream());
			WriteStream(msg);
			
			msg = (Message) ReadStream();
			msg.printMessage();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		this.master = master;
		
		Thread t = new Thread(this);
		t.start();
	}

	public void run(){

		while(true){
			try{
				Message msg = (Message) ReadStream();
				DecipherMsg(msg);
			}
			catch(Exception e){
			}
		}
	}
	public Object ReadStream(){
		try{
			return ois.readObject();
		}
		catch(Exception e){
		}
		return null;
	}

	public void WriteStream(Object object){

		try {
			oos.writeObject(object);
			oos.reset();
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}	
	}
	
	public void addDirectory(String path){
		
		String [] split = path.split(":?\\\\");
		
		TFSDir current = master.root;
		
		for(int i = 0; i < split.length - 1; i++){
			int index = -1;
			String str = split[i];
			
			for(int j = 0; j < current.subDirs.size(); j++){
				TFSDir dir = current.subDirs.get(j);
				if(dir.name.equals(str)){
					index = j;
					break;
				}
			}	
			
			current = current.subDirs.get(index);
		}
		
		int lastSlash = path.lastIndexOf("\\");
		String name = path.substring(lastSlash+1);
		
		current.subDirs.add(new TFSDir(name, current));
	
	}
	
	public RETVAL CheckPathDirectory(String absolutePath){
		
		String [] split = absolutePath.split(":?\\\\");	
		TFSDir current = master.root;
		for(int i = 0; i < split.length - 1; i++){
			int index = -1;
			String str = split[i];
			
			for(int j = 0; j < current.subDirs.size(); j++){
				TFSDir dir = current.subDirs.get(j);
				if(dir.name.equals(str)){
					index = j;
					break;
				}
			}
			
			if(index == -1)
				return RETVAL.ERROR;
			
			current = current.subDirs.get(index);
			
		}
		
		for(TFSDir dir: current.subDirs){
			if(dir.name.equals(split[split.length-1]))
				return RETVAL.EXISTS;
		}
		
		return RETVAL.NOT_FOUND;
	}
	
/*	private RETVAL deleteDir(String absolutePath){
		
		String [] split = absolutePath.split(":?\\\\");	
		TFSDir current = master.root;
		for(int i = 0; i < split.length - 1; i++){
			int index = -1;
			String str = split[i];
			
			for(int j = 0; j < current.subDirs.size(); j++){
				TFSDir dir = current.subDirs.get(j);
				if(dir.name.equals(str)){
					index = j;
					break;
				}
			}
			
			if(index == -1)
				return RETVAL.NOT_FOUND;
			
			current = current.subDirs.get(index);
			
		}
		
		boolean exists = false;
		
		for(TFSDir dir: current.subDirs){
			if(dir.name.equals(split[split.length-1]))
				current = dir;
				exists = true;
		}
		
		if(!exists)
			return RETVAL.NOT_FOUND;
		else{
			
			DeleteDirectories(current);
		}
		
		return RETVAL.OK;
		
	}
	*/

	
	private RETVAL delete(String path) {
		File dir = new File(path);
		
		System.out.println("PATH: " + path);
		
	    if (!dir.exists()) return RETVAL.NOT_FOUND;
	    // Delete if dirPath is a directory
	    if (dir.isDirectory()) {
	    	for (File f : dir.listFiles()) delete(f.getPath());
	    	dir.delete();

	    	//Delete the dir from the TFSDir root
	    	String[] split = path.split(":?////");
	    	TFSDir current = master.root;
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
	       TFSDir current = master.root;
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
	
	public RETVAL createDir(String absolutePath){
		
		RETVAL ret = CheckPathDirectory(absolutePath);
		
		if(ret == RETVAL.NOT_FOUND){
			String path = master.root.name + "\\" + absolutePath;
			File dir = new File(path);
			dir.mkdir();
			
			addDirectory(absolutePath);
		}
		
		return ret;
	}
	
	//This reads in all the message coming in from the clients. It then matches the string and performs an action.
	public void DecipherMsg(Message msg) {
		
		msg.printMessage();
		
		switch(msg.operation){
		
		case CREATE_DIR:
			msg.retValue = createDir(msg.absolutePath);
			WriteStream(msg);
			break;
			
		case DELETE_DIR:
			msg.retValue = delete(master.root.name + "\\" + msg.absolutePath);
			WriteStream(msg);
			break;
			
		case DELETE_FILE:
			msg.retValue = delete(msg.absolutePath);
			WriteStream(msg);
			break;
			
		case APPEND:
			
			//TODO SHOULD TECHINCALLY JUST SEND THE INFO BACK AND SEE IF IT EXISTS FOR THE PERSON
			break;
		}
		
		
			
		
	}
	
}
