
package message;

import java.io.Serializable;
import java.util.ArrayList;

import enums.*;


public class Message implements Serializable {
	
	public Retval retValue;
	public OPERATION operation;
	public SENDER sender;
	
	public String absolutePath;
	
	public long fileSize;
	public int numReplications;
	
	public byte [] bytes;
	
	public ArrayList<String> driveNamesToSave;
	
	public Message(){}
	
	public Message(OPERATION operation, SENDER sender, String absolutePath){
		this.operation = operation;
		this.sender = sender;
		this.absolutePath = absolutePath;
	}
	
	public void printMessage(){
		System.out.println("From " + sender.toString() + ": " + operation.toString() + ": " + absolutePath);
	}
	
}
