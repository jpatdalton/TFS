package Threads;

import java.io.*;
import java.net.*;

import Enums.OPERATION;
import Enums.SENDER;
import Message.*;

//Every client needs to extend Client to become connected to the server.
//The DecipherMsg(String) is to be implements in all clients, so that clients can constantly listen for messages.
public abstract class Client implements Runnable {

	Socket socket = null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;

	//Constructor
	public Client(){
		SetupConnection();
		//Thread t = new Thread(this);
		//t.start();
	}

	//Deciphering the message.
	public abstract void DecipherMsg(Message msg);

	//Setting up the connect and connecting to host.
	public void SetupConnection(){
		try {
			socket = new Socket("localhost", 3434);
		
			oos = new ObjectOutputStream(socket.getOutputStream());
			
			Message msg = new Message(OPERATION.CREATE_DIR, SENDER.CLIENT, "IP", "PATH");
			Write(msg);
			
			ois = new ObjectInputStream(socket.getInputStream());
			msg = (Message) ReadStream();
			msg.printMessage();
			
			msg.operation = OPERATION.APPEND;
			
			Write(msg);
			
			msg.senderIP = "IPNUM6";
			
			Write(msg);
			
			
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void run(){}
	
	/*
	//Listen for messages.
	public void run() {
		while(true){
			try{
				Message msg = (Message) ReadStream();
				msg.printMessage();
				DecipherMsg(msg);
			}catch(Exception e){
			}
		}
	}*/
	
	

	//Read stream.
	public Object ReadStream(){
		try{
			return ois.readObject();
			
		}
		catch(Exception e){
			System.out.println("You Have Been Disconnected From the Server");
		}
		return null;
	}

	//Writestream to write to server.
	public void Write(Object object){
		try{
			oos.writeObject(object);
			oos.flush();
			
			//oos.reset();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

}