package helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class helper provides the program the capability of logging
 * @author Nguyen
 *
 */
public class LogHelper {
	// log-record file name is set up internally
	public static final String LOG_RECORD_PATH = "LOG_RECORDS.txt";
	
	// use for debug purpose
	public static final String LOG_CHUNKS = "LOG_CHUNKS.txt";
	
	public static boolean debug = true;

	/**
	 * Put a record into log record file
	 * @param log
	 * @return
	 */
	public static boolean log(String log, String path) {
		try {
			// create or open file 
			FileWriter fstream = new FileWriter(path, true);
			BufferedWriter out = new BufferedWriter(fstream);
			
			// append a log record to file 
			out.write(log + "\r\n");
			
			// close the output stream
			out.close();
			
			return true;
		} catch (Exception e){	// catch exception if any
			System.err.println("Error: " + e.getMessage());
		}		
		
		return false;
	}
	
	public static List<String> getLogs(String path) {
		List<String> logs = new ArrayList<String>();
		
		try {
			// create or open file 
			FileReader fstream = new FileReader(path);
			BufferedReader in = new BufferedReader(fstream);
			
			// append a log record to file
			String line = "";
			
			while ((line = in.readLine()) != null) {
				logs.add(line);
			}
			
			// close the output stream
			in.close();
		} catch (Exception e){	// catch exception if any
			System.out.println("Error: " + e.getMessage());
		}		
		
		return logs;		
	}
}
