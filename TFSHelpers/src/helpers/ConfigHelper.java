package helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigHelper {
	public static final String CONFIG_FILE = "config.txt";
	
	/**
	 * Get the value of a setting by providing its key
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {	
		String value = null;
		
		try {
			FileReader fr = new FileReader(CONFIG_FILE);
			BufferedReader br = new BufferedReader(fr);
			
			String line;
			
			while ((line = br.readLine()) != null) {
				if (line.startsWith(key)) {
					String[] strs = line.split(" = ");
					
					value = strs[1];
				}
			}
			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}
	
	/**
	 * Get list of chunk servers
	 * @return
	 */
	public static List<String> getChunkServers() {
		List<String> chunkServers = new ArrayList<String>();
		
		try {
			FileReader fr = new FileReader(CONFIG_FILE);
			BufferedReader br = new BufferedReader(fr);
			
			String line;
			
			while ((line = br.readLine()) != null) {
				if (line.startsWith("chunkserver")) {
					String[] strs = line.split(" = ");
					
					chunkServers.add(line.substring(strs[0].length() + " = ".length()));
				}
			}
			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return chunkServers;
	}
}
