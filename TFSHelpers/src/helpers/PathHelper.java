package helpers;

public class PathHelper {
	/**
	 * Get nodes from a specific path
	 * @param path
	 * @return
	 */
	public static String[] getNodes(String path) {
		if (path.contains("/"))
			return path.split("/");
		
		return null;
	}
}
