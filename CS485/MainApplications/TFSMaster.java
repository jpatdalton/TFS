package MainApplications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import Message.*;
import Enums.*;

import FileSystem.*;
import Threads.*;

import Helpers.*;

//This is the server, where all the clients connect to.

public class TFSMaster implements Runnable {

	ArrayList<ServerClient> clients = new ArrayList();

	ServerSocket ss = null;

	public TFSDir root;

	public TFSMaster(){

		CreateRoot();
		
		//accept 6 clients.
		SetupConnections();
		Thread t = new Thread(this);
		t.start();

	}	

	private void CreateLogOrLoadFromLog() {

		String folder = "C:\\485log";
		File dir = new File(folder);
		
		dir.mkdir();
		
		
		File file = new File(LogHelper.LOG_RECORD_PATH);

		if(!file.exists()){
			try {
				
				file.createNewFile();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			try{
				
				
				
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {

					String op = line.substring(0, line.indexOf(" "));
					String path = line.substring(line.indexOf(" ")+1);

					ServerClient client = new ServerClient(this);

					switch(op){

					case "CREATE_DIR":
						client.createDir(path);
						break;

					case "DELETE_DIR":
						client.delete(root.name + "\\" + path);
						break;

					case "DELETE_FILE":
						client.delete(root.name + "\\" + path);
						break;

					case "CREATE_FILE":
						client.createFile(path, 1000);
						break;

					case "APPEND":

						//TODO SHOULD TECHINCALLY JUST SEND THE INFO BACK AND SEE IF IT EXISTS FOR THE PERSON

						//TODO create log

						break; 
					} 
					
				}
				br.close();
			}
			catch(IOException e){

				e.printStackTrace();

			}	
		}
	}

	public void CreateRoot(){

		File theDir = new File("C:\\CS485");
		boolean result = theDir.mkdir();

		// TODO CLEAN OUT ROOT PER EACH RUN		
		root = new TFSDir("C:\\CS485", null);


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
			SetupThreads(socket);

		} 
		catch (Exception e) {
			e.printStackTrace();
		}	

		Thread t = new Thread(this);
		t.start();

	}

	//main
	public static void main(String[] args) {
		TFSMaster master = new TFSMaster();
	}

	@Override
	public void run() {

		CreateLogOrLoadFromLog();
	}

}
