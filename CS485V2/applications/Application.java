package applications;

import helpers.SerializationHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import message.Message;
import enums.OPERATION;
import enums.Retval;
import enums.SENDER;

public class Application {

	public static Retval writeFileLocally(String filePath, byte bytes[]){

		try {

			System.out.println("APPEND: " + filePath);

			//filePath = "C:\\CS485\\" + filePath;

			File file = new File(filePath);

			//TODO SHOULD RECEIVE INFORMATION FROM SERVER AND BE ADDED TO END		
			//Create file if it doesnt exist
			if(!file.exists()){
				return Retval.NOT_FOUND;
			} else {

				FileOutputStream output = new FileOutputStream(filePath, true);
				try {
					output.write(bytes);
				} finally {
					output.close();
				}

				return Retval.OK;
			}
		} catch (Exception e) {
			return Retval.ERROR;
		}	

	}

	public static String NormalizeString(String dirPath){
		
		dirPath = dirPath.replace("/", "\\");
		return dirPath;
		
	}
	
	public static Message readFileLocally(String filePath){

		byte[] data = SerializationHelper.getBytesFromFile(filePath);

		Message msg = new Message(OPERATION.READ_FILE, SENDER.CHUNK_SERVER, filePath);

		if(data == null)
			msg.retValue = Retval.NOT_FOUND;
		else
			msg.retValue = Retval.OK;

		msg.bytes = data;


		return msg;

	}
	
	public static Retval createFileLocally(String filePath){

		File file = new File(filePath);
		if(file.exists())
			return Retval.EXISTS;

		try {
			file.createNewFile();
			return Retval.OK;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Retval.CLIENT_ERROR;
		}

	}
	
	public static ArrayList<String> getSubdirectories(String absolutePath){

		File file = new File(absolutePath);
		ArrayList<String> subdirectories = new ArrayList();

		if(file.exists() && file.isDirectory()){
			File [] names = file.listFiles();
			for(File f: names){
				if(f.isDirectory()){
					String path = f.getAbsolutePath();
					int index = path.indexOf("\\");
					path = path.substring(index + 1);
					index = path.indexOf("\\");
					path = path.substring(index +1);
					subdirectories.add(path);
				}
			}
		}

		return subdirectories;

	}
	
}
