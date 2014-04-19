package helpers;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;

/**
 * This class helper provides the program the capability of logging
 * @author Nguyen
 *s
 */
public class LogHelper {
	// log-record file name is set up internally
	public static final String LOG_RECORD_PATH = "C:/485log/LOG_RECORDS.txt";
	
	// use for debug purposes
	private static final String LOG_DEBUG = "C:/485log/LOG_DEBUG.txt";
	
	public static boolean debug = true;

	/**
	 * Put a record into log record file
	 * @param log
	 * @return
	 */
	public static boolean logRecord(String log) {
		try {
			// create or open file 
			FileOutputStream output = new FileOutputStream(LOG_RECORD_PATH, true);
			
			// append a log record to file 
			log = log + "\r\n";
			output.write(log.getBytes());
			
			// close the output stream
			output.close();
			
			return true;
		} catch (Exception e){	// catch exception if any
			System.err.println("Error: " + e.getMessage());
		}		
		
		return false;
	}
	
	/**
	 * Log a message
	 * @param message
	 * @return
	 */
	public static boolean log(String message) {
		if (debug) {
			try {
				// create or open file 
				FileWriter fstream = new FileWriter(LOG_DEBUG);
				BufferedWriter out = new BufferedWriter(fstream);
				
				// append a log record to file 
				out.write(message);
				
				// close the output stream
				out.close();
				
				return true;
			} catch (Exception e){	// catch exception if any
				System.err.println("Error: " + e.getMessage());
			}		
		}
		
		return false;		
	}
}
