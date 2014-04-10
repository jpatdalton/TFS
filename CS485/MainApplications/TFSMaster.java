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
