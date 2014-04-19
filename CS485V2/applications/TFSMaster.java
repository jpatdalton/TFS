package applications;

import helpers.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.file.Files;
import java.util.*;

import threads.*;
import message.*;
import enums.*;
import filesystem.*;

//This is the server, where all the clients connect to.

public class TFSMaster implements Runnable {

	ArrayList<ServerClient> clients = new ArrayList();

	List<MessageHelper> msgSchedule = Collections.synchronizedList(new ArrayList<MessageHelper>());
	
	
	ServerSocket ss = null;

	public TFSDir root;

	public TFSMaster(){

		CreateRoot();
		CreateLog();
		RunFileCrawler("C:\\CS485\\", root);

		Thread t = new Thread(this);
		t.start();

		//accept 6 clients.
		SetupConnections();


	}	

	public void AddToSchedule(MessageHelper msg){

		synchronized(msgSchedule){

			msgSchedule.add(msg);

		}


	}

	private void CreateLog() {

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
	}

	public void RunFileCrawler(String physicalPath, TFSDir current){

		File dir = new File(physicalPath + current.absolutePath);

		for(File f: dir.listFiles()){
			if(f.isFile()){
				int lastIndex = f.getAbsolutePath().lastIndexOf("\\");
				String name = f.getAbsolutePath().substring(lastIndex + 1);
				TFSFile file = new TFSFile(name, current);
				current.subFiles.add(file);
			}
			else if(f.isDirectory()){
				int lastIndex = f.getAbsolutePath().lastIndexOf("\\");
				String name = f.getAbsolutePath().substring(lastIndex + 1);
				TFSDir directory = new TFSDir(name, current);
				current.subDirs.add(directory);

				RunFileCrawler(physicalPath, directory);
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

			while(true){
				System.out.println("Waiting for Connection");
				Socket socket = ss.accept();
				SetupThreads(socket);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public void addDirectory(String path){

		String [] split = path.split(":?\\\\");

		TFSDir current = root;

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

	public void addFile(String path){

		String [] split = path.split(":?\\\\");

		TFSDir current = root;

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

		current.subFiles.add(new TFSFile(name, current));

	}

	public Retval CheckPathDirectory(String absolutePath){

		String [] split = absolutePath.split(":?\\\\");	
		TFSDir current = root;
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
				return Retval.ERROR;

			current = current.subDirs.get(index);

		}

		for(TFSDir dir: current.subDirs){
			if(dir.name.equals(split[split.length-1]))
				return Retval.EXISTS;
		}

		return Retval.NOT_FOUND;
	}

	public Retval CheckPathFile(String absolutePath){

		String [] split = absolutePath.split(":?\\\\");	
		TFSDir current = root;
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
				return Retval.ERROR;

			current = current.subDirs.get(index);

		}

		for(TFSFile file: current.subFiles){
			if(file.name.equals(split[split.length-1]))
				return Retval.EXISTS;
		}

		return Retval.NOT_FOUND;
	}

	public Retval deleteDir(TFSDir dir){

		for(TFSFile f: dir.subFiles){

			File file = new File("C:\\CS485\\" + f.absolutePath);

			if(file.exists())
				file.delete();

		}

		dir.subFiles.clear();

		for(TFSDir d: dir.subDirs){
			deleteDir(d);
		}

		dir.subDirs.clear();

		File directory = new File("C:\\CS485\\" + dir.absolutePath);
		if(directory.exists())
			directory.delete();

		return Retval.OK;

	}

	public Retval delete(String path, boolean isDir) {
		File dir = new File(path);

		System.out.println("PATH: " + path);

		if (!dir.exists()) return Retval.NOT_FOUND;
		// Delete if dirPath is a directory
		if (isDir) {

			//Delete the dir from the TFSDir root
			String[] split = path.split(":?\\\\");
			TFSDir current = root;
			for (int i = 0; i < split.length-1; i++) {
				for (int j = 0; j < current.subDirs.size(); j++) {
					if (current.subDirs.get(j).name.equals(split[i]))
						current = current.subDirs.get(j);
				}
			}


			TFSDir toBeDeleted= null;

			for(int i = 0; i < current.subDirs.size(); i++)
			{
				if(current.subDirs.get(i).name.compareTo(split[split.length-1]) == 0){
					toBeDeleted = current.subDirs.get(i);
					break;
				}
			}

			if(toBeDeleted == null)
				return Retval.NOT_FOUND;

			deleteDir(toBeDeleted);
			current.subDirs.remove(toBeDeleted);

			return Retval.OK;

			// Delete if dirPath is a file
		} else {

			System.out.println(dir.getAbsolutePath());


			boolean deleted = dir.delete();
			System.out.println("IN HERE");
			if(deleted)
				System.out.println("Deleted");

			//Delete the file from the TFSDir root
			String[] split = path.split(":?\\\\");
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
			return Retval.OK;
		} 
	}

	public Retval createDir(String absolutePath){

		Retval ret = CheckPathDirectory(absolutePath);

		if(ret == Retval.NOT_FOUND){
			String path = root.name + "\\" + absolutePath;
			File dir = new File(path);
			dir.mkdir();

			addDirectory(absolutePath);

			ret = Retval.OK;

		}

		return ret;
	}

	public Retval createFile(String absolutePath, long fileSize){

		Retval ret = CheckPathFile(absolutePath);
		if(ret == Retval.NOT_FOUND){

			String path = root.name + "\\" + absolutePath;
			try {

				//File file = new File(path);

				File file = new File(path);
				file.createNewFile();


				//RandomAccessFile f = new RandomAccessFile(path, "rwd");
				//f.setLength(fileSize);
				//f.close();
			} 
			catch (Exception e) {
				System.err.println(e);
				return Retval.SERVER_ERROR;
			}

			addFile(absolutePath);

			ret = Retval.OK;

		}
		return ret;
	}

	public void DecipherMsg(MessageHelper helpMsg) {


		Message msg = helpMsg.msg;

		msg.printMessage();

		switch(msg.operation){

		case CREATE_DIR:
			msg.retValue = createDir(msg.absolutePath);

			helpMsg.client.WriteStream(msg);
			break;

		case DELETE_DIR:
			msg.retValue = delete(root.name + "\\" + msg.absolutePath, true);


			helpMsg.client.WriteStream(msg);
			break;

		case DELETE_FILE:
			msg.retValue = delete(root.name + "\\" + msg.absolutePath, false);


			helpMsg.client.WriteStream(msg);
			break;

		case CREATE_FILE:
			msg.retValue = createFile(msg.absolutePath, msg.fileSize);

			helpMsg.client.WriteStream(msg);
			break;

		case APPEND:

			//TODO SHOULD TECHINCALLY JUST SEND THE INFO BACK AND SEE IF IT EXISTS FOR THE PERSON


			break;
		}

		if(msg.retValue == Retval.OK)
		{		
			String command;
			command = msg.operation.toString();

			String path;
			path = msg.absolutePath;

			String logString;
			logString = command + " " + path;

			LogHelper.logRecord(logString);
		}



	}

	//main
	public static void main(String[] args) {
		TFSMaster master = new TFSMaster();
	}

	@Override
	public void run() {

		while(true){

			synchronized(msgSchedule){

				if(msgSchedule.size() != 0){

					DecipherMsg(msgSchedule.get(0));
					msgSchedule.remove(0);

				}
			}
		}

	}

}
