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
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
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
				System.out.println(e.getMessage());
				break;
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
	
	public RETVAL CheckPath(String absolutePath){
		
		String [] split = absolutePath.split(":?\\\\");
		
		int index;
		TFSDir current = master.root;
		
		for(int i = 0; i < split.length - 1; i++){
			String str = split[i];
			index = current.subDirs.indexOf(str);
			if(index == -1)
				return RETVAL.ERROR;
			
			current = current.subDirs.get(index);
			
			System.out.println("FUCK");
			
		}
		
		if(current.subDirs.size() == 0)
			return RETVAL.OK;
		else if(current.subDirs.contains(split[split.length-1]))
			return RETVAL.EXISTS;
		else
			return RETVAL.OK;
		
	}
	
	//This reads in all the message coming in from the clients. It then matches the string and performs an action.
	public void DecipherMsg(Message msg) {
		
		msg.printMessage();
		System.out.println("HELLO");
		
		switch(msg.operation){
		
		case CREATE_DIR:
			
			System.out.println("HI");
			RETVAL ret = CheckPath(msg.absolutePath);
			System.out.println("HI");
			//TODO MAKE DIRECTORY OR WHATEVER
			msg.retValue = ret;
			
			WriteStream(msg);
			
			break;
		}
	}
	
}
