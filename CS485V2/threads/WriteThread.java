
package threads;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.Timer;

//The WriteThread is created in the Server. 
//This write thread will write messages to the Clients.

public class WriteThread{
	ObjectOutputStream oos = null;

	public WriteThread(Socket s)
	{
		try{
			oos = new ObjectOutputStream(s.getOutputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	//WriteStream to write an object to clients.
	public void WriteStream(Object object){

		try {
			oos.writeObject(object);
			oos.reset();
		}
		catch(Exception e){
		}	
	}	
}