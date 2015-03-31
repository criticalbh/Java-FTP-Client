package ftpUpload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;



public class FTPUpload {
	/********************
	 * Variable members
	 *******************/
	private String Host, Username, Password;
	private File FileToUpload;
	private Socket Socket = null;
	private BufferedReader Reader = null;
	private BufferedWriter Writer = null;

	/********************
	 * Constructor
	 *******************/
	public FTPUpload(String host, String user, String pass, File fileToUpload) {
		Host 			= host;
		Username 		= user;
		Password 		= pass;
		FileToUpload 	= fileToUpload;
		try {
			connect();
		} catch (IOException e) {
			errorOccured("", e.getMessage());
		}
	}

	/********************
	 * Function members
	 *******************/
	
	/*
	 * Connects to FTP server
	 */
	private void connect()
			throws IOException {
		if (Socket != null) {
			errorOccured("", 
					"Connection already established.");
		}
		
		int port = 21;
		
		Socket = new Socket(Host, port);
		Reader = new BufferedReader(new InputStreamReader(
				Socket.getInputStream()));
		Writer = new BufferedWriter(new OutputStreamWriter(
				Socket.getOutputStream()));

		String response = readLine();
		
		if (!response.startsWith("220 ")) {
			errorOccured(response, 
					"Unknown response from FTP server: ");
		}

		sendLine("USER " + Username);

		response = readLine();
		if (!response.startsWith("331 ")) {
			errorOccured(response, "Wrong username: ");
		}

		sendLine("PASS " + Password);

		response = readLine();
		if (!response.startsWith("230 ")) {
			errorOccured(response, "Wrong password: ");
		}
	}

	/*
	 * Disconnects from the FTP server.
	 */
	public void disconnect() throws IOException {
		try {
			sendLine("QUIT");
		} finally {
			Socket = null;
		}
	}
	
	/*
	 * Method for error handling
	 */
	public void errorOccured(String response, String message){
		System.out.println(message + response);
		System.exit(0);
	}
	
	/*
	 * return new socket initialized by ip and port issued by FTP server
	 */
	private Socket dataSocket(int opening, int closing, String response) 
			throws IOException {
		String ip = null;
		int port = -1;
		
		if (closing > 0) {
			String dataLink = response.substring(opening + 1, closing);
			StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
			try {
				ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
						+ tokenizer.nextToken() + "." + tokenizer.nextToken();
				port = Integer.parseInt(tokenizer.nextToken()) * 256
						+ Integer.parseInt(tokenizer.nextToken());
			} catch (Exception e) {
				errorOccured(response, "Expected another response: ");
			}
		}
		return new Socket(ip, port);
	}
	
	/*
	 * stores a file to FTP server in passive mode
	 */
	public void stor() throws IOException {
		InputStream inputStream = new FileInputStream(FileToUpload);
		BufferedInputStream input = new BufferedInputStream(inputStream);
		
		
		// Enter passive mode  
		sendLine("PASV");
		String response = readLine();		
		int opening = response.indexOf('(');
		int closing = response.indexOf(')', opening + 1);
		
		
		// Command to store a file to remote host
		sendLine("STOR " + FileToUpload.getName());

		
		// Initialize new independent connection to host and port
		// that server provided to us.
		Socket dataSocket = dataSocket(opening, closing, response);

		
		// Initialize buffered output stream on dataSocket 
		BufferedOutputStream output = new BufferedOutputStream(
				dataSocket.getOutputStream());
		
		
		// Size of byte buffer 1MB
		byte[] buffer = new byte[1024 * 1024];
		int bytesRead = 0;
		
		// Get time when upload start
		Long startTime = System.currentTimeMillis();
		
		//float currentBytes;
		float uploaded = 0;
		
		// Reads up to byte.length bytes of data from this input stream into an array of bytes.
		while ((bytesRead = input.read(buffer)) != -1) {
			//currentBytes = uploaded;
			
			
			// Write read bytes to output stream		 
			output.write(buffer, 0, bytesRead);
			
			
			// Track num of uploaded bytes
			uploaded += bytesRead;
			
			/*
			 * This should output upload speed every second
			System.out.println(FileToUpload.getName() + ": " 
					+ (uploaded - currentBytes)/1024 +" KB/s");
			*/
		}
		
		// Get time when upload finish
		Long endTime = System.currentTimeMillis();
		
		// Calculate duration
		double timetook = ((double) (endTime - startTime)) / 1000;

		
		// Calculate KB
		double KBTransfered = uploaded / timetook;

		
		// Save average speed in KB/s for this client
		Reports.addStatistics(
				FileToUpload.getName(), 
				KBTransfered/1024, 
				timetook);

		
		// Force write the file, close input and output streams
		output.flush();
		output.close();
		input.close();
	}

	/*
	 * Sends a raw command to the FTP server.
	 */	
	private void sendLine(String line) throws IOException {
		if (Socket == null) {
			errorOccured("", "Please connect.");
		}
		try {
			Writer.write(line + "\r\n");
			Writer.flush();
		} catch (IOException e) {
			Socket = null;
			errorOccured("",e.getMessage());
		}
	}

	/*
	 * Reads a line of text. In this case response from FTP
	 */
	private String readLine() throws IOException {
		String line = Reader.readLine();
		return line;
	}
	
	/*
	 * Method to be started by thread
	 */
	public static void sendFile(String host, String username,
		String password, File fileName) throws IOException {
		
	    FTPUpload client = new FTPUpload(host, username, password, fileName);
		client.stor();
		client.disconnect();
	}

}
