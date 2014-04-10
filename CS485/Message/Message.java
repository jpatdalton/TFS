
package Message;

import java.io.Serializable;
import Enums.*;


public class Message implements Serializable {
	
	public RETVAL retValue;
	public OPERATION operation;
	public SENDER sender;
	
	public String senderIP;
	
	public String absolutePath;
	
	public long fileSize;
	public int numReplications;
	
	public byte [] bytes;
	
	public Message(){}
	
	public Message(OPERATION operation, SENDER sender, String senderIP, String absolutePath){
		this.operation = operation;
		this.sender = sender;
		this.senderIP = senderIP;
		this.absolutePath = absolutePath;
	}
	
	public void printMessage(){
		System.out.println("From " + sender.toString() + " " + senderIP + ": " + operation.toString() + ": " + absolutePath);
	}
	
}
