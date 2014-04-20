package socket;

import helpers.LogHelper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class SocketIO {
	Socket sock = null;
	
	BufferedOutputStream out = null;
	DataInputStream in = null;	
	
	public SocketIO(String ipaddress, int port) {
		try {
			this.sock = new Socket(ipaddress, port);		

			sock.setKeepAlive(true);
			sock.setSoTimeout(0);
			
			out = new BufferedOutputStream(sock.getOutputStream());
			in = new DataInputStream(sock.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SocketIO(Socket socket) {
		this.sock = socket;
		
		// get input and ouput stream
		try {
			sock.setKeepAlive(true);
			sock.setSoTimeout(0);
			
			out = new BufferedOutputStream(sock.getOutputStream());
			in = new DataInputStream(sock.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * reads a line
	 * intentionally not using the deprecated readLine method from DataInputStream 
	 * 
	 * @return String that was read in
	 * @throws IOException if io problems during read
	 */
	public String readLine() throws IOException {
		if ( sock == null || !sock.isConnected() ) {
			if (LogHelper.debug)
				System.out.println("Problem with socket in readLine()");
			
			return null;
		}

		byte[] b = new byte[1];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		boolean eol = false;

		while ( in.read( b, 0, 1 ) != -1 ) {
			if ( b[0] == 13 ) {
				eol = true;
			}
			else {
				if ( eol ) {
					if ( b[0] == 10 )
						break;

					eol = false;
				}
			}

			// cast byte into char array
			bos.write( b, 0, 1 );
		}

		if ( bos == null || bos.size() <= 0 ) {
			throw new IOException( "++++ Stream appears to be dead, so closing it down" );
		}

		// else return the string
		return bos.toString().trim();
	}
	
	/** 
	 * reads up to end of line and returns nothing 
	 * 
	 * @throws IOException if io problems during read
	 */
	public void clearEOL() throws IOException {
		if ( sock == null || !sock.isConnected() ) {
			if (LogHelper.debug)
				System.out.println("Problem with socket in clearEOL()");
		}

		byte[] b = new byte[1];
		boolean eol = false;
		while ( in.read( b, 0, 1 ) != -1 ) {

			// only stop when we see
			// \r (13) followed by \n (10)
			if ( b[0] == 13 ) {
				eol = true;
				continue;
			}

			if ( eol ) {
				if ( b[0] == 10 )
					break;

				eol = false;
			}
		}
	}
	
	/** 
	 * reads length bytes into the passed in byte array from dtream
	 * 
	 * @param b byte array
	 * @throws IOException if io problems during read
	 */
	public int read( byte[] b ) throws IOException {
		if ( sock == null || !sock.isConnected() ) {
			if (LogHelper.debug)
				System.out.println("Problem with socket in read()");			
		}

		int count = 0;
		while ( count < b.length ) {
			int cnt = in.read( b, count, (b.length - count) );
			count += cnt;
		}

		return count;
	}	
	
	/** 
	 * flushes output stream 
	 * 
	 * @throws IOException if io problems during read
	 */
	public void flush() throws IOException {
		if ( sock == null || !sock.isConnected() ) {
			if (LogHelper.debug)
				System.out.println("Problem with socket in flush()");			
		}
		out.flush();
	}
	
	/** 
	 * writes a byte array to the output stream
	 * 
	 * @param b byte array to write
	 * @throws IOException if an io error happens
	 */
	public void write( byte[] b ) throws IOException {
		if ( sock == null || !sock.isConnected() ) {
			if (LogHelper.debug)
				System.out.println("Problem with socket in write()");			
		}
		out.write( b );
	}
	
	public void close() throws IOException {
		if (sock == null && !sock.isConnected()) {
			sock.close();
		}
	}
	
	public InetAddress getLocalAddress() {
		if (sock != null && sock.isConnected()) {
			return sock.getLocalAddress();
		}

		return null;
	}
	
	public InetAddress getInetAddress() {
		if (sock != null && sock.isConnected()) {
			return sock.getInetAddress();
		}

		return null;
	}
	
	public int getLocalPort() {
		if (sock != null && sock.isConnected()) {
			return sock.getLocalPort();
		}

		return -1;
	}
	
	public int getPort() {
		if (sock != null && sock.isConnected()) {
			return sock.getPort();
		}

		return -1;
	}
}
