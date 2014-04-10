package Threads;

import java.net.*;
import java.io.*;
import java.net.Socket;
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
		
		current.subDirs.add(new TFSDir(name));
	
	}
	
	public RETVAL CheckPath(String absolutePath){
		
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
		
		return RETVAL.OK;
	}
	
	//This reads in all the message coming in from the clients. It then matches the string and performs an action.
	public void DecipherMsg(Message msg) {
		
		msg.printMessage();
		
		switch(msg.operation){
		
		case CREATE_DIR:
			
			RETVAL ret = CheckPath(msg.absolutePath);
			
			if(ret == RETVAL.OK){
				String path = master.root.name + "\\" + msg.absolutePath;
				File dir = new File(path);
				dir.mkdir();
				
				addDirectory(msg.absolutePath);
			}
			
			msg.retValue = ret;
			
			WriteStream(msg);
			
			break;
		}
	}
	
}
