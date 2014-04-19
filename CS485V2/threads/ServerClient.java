package threads;

import helpers.*;

import java.net.*;
import java.io.*;
import java.net.Socket;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import message.*;
import applications.*;
import enums.*;
import filesystem.*;

public class ServerClient implements Runnable {

	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;

	TFSMaster master;

	public ServerClient(TFSMaster master){

		this.master = master;
	}

	public ServerClient(Socket s, TFSMaster master)
	{
		try{
			ois = new ObjectInputStream(s.getInputStream());

			Message msg = (Message) ReadStream();
			msg.printMessage();

			oos = new ObjectOutputStream(s.getOutputStream());
			msg.sender = SENDER.MASTER;
			WriteStream(msg);

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

				if(msg!=null)
				{
					
					MessageHelper helpMsg = new MessageHelper(msg, this);
					
					master.AddToSchedule(helpMsg); //DecipherMsg(msg);

				}
				
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




	//This reads in all the message coming in from the clients. It then matches the string and performs an action.


}
