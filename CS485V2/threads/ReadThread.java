package threads;

import java.io.ObjectInputStream;
import java.net.Socket;

import message.*;

public class ReadThread implements Runnable{

	ObjectInputStream ois;

	public ReadThread(Socket s)
	{
		try{
			ois = new ObjectInputStream(s.getInputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}

		Thread t = new Thread(this);
		t.start();
	}

	public void run(){

		while(true){
			try{
				Message msg = (Message) ReadStream();
				msg.printMessage();
				DecipherMsg(msg);
			}
			catch(Exception e){
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

	//This reads in all the message coming in from the clients. It then matches the string and performs an action.
	public void DecipherMsg(Message msg) {

		msg.printMessage();

	}
}