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



public class FTPClient {

	public FTPClient() {
		
	}

	/**
	 * Connects to an FTP server and logs in with the supplied username and
	 * password.
	 */
	
	public void connect(String host, String user, String pass)
			throws IOException {
		if (socket != null) {
			throw new IOException(
					"Connection already established.");
		}
		int port = 21;
		/*
		 * Init socket, reader and writer streams
		 */
		socket = new Socket(host, port);
		reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));

		String response = readLine();
		if (!response.startsWith("220 ")) {
			throw new IOException(
					"Unknown response from FTP Server: "
							+ response);
		}

		sendLine("USER " + user);

		response = readLine();
		if (!response.startsWith("331 ")) {
			throw new IOException(
					"Wrong usernaem: "
							+ response);
		}

		sendLine("PASS " + pass);

		response = readLine();
		if (!response.startsWith("230 ")) {
			throw new IOException(
					"Wrong password: "
							+ response);
		}
	}

	/*
	 * Disconnects from the FTP server.
	 */
	public void disconnect() throws IOException {
		try {
			sendLine("QUIT");
		} finally {
			socket = null;
		}
	}

	/*
	 * return new socket initialized by ip and port issued by FTP server
	 */
	private Socket dataSocket(int opening, int closing, String response) 
			throws IOException{
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
				throw new IOException(
						"Expected another response: "
								+ response);
			}
		}
		return new Socket(ip, port);
	}
	
	/*
	 * stores a file to FTP server in passive mode
	 */
	public void stor(File file) throws IOException {
		if (file.isDirectory()) {
			throw new IOException("Choose file instead of dir.");
		}

		String filename = file.getName();
		InputStream inputStream = new FileInputStream(file);
		BufferedInputStream input = new BufferedInputStream(inputStream);
		
		/*
		 * Save file name for this client
		 */
		Reports.Names.add(filename);
	
		/*
		 * Enter passive mode 
		 */
		sendLine("PASV");
		String response = readLine();		
		int opening = response.indexOf('(');
		int closing = response.indexOf(')', opening + 1);
		
		/*
		 * Store a file on the remote host 
		 */
		sendLine("STOR " + filename);

		/*
		 * Initialize new independent connection to host and port
		 * that server provided to us.
		 */
		Socket dataSocket = dataSocket(opening, closing, response);

		/*
		 * Initialize buffered output stream 
		 * on dataSocket
		 */
		BufferedOutputStream output = new BufferedOutputStream(
				dataSocket.getOutputStream());
		
		/*
		 * Size of byte buffer 1MB
		 */
		byte[] buffer = new byte[1024];
		int bytesRead = 0;
		/*
		 * Get time when upload start
		 */
		Long startTime = System.currentTimeMillis();
		double uploaded = 0;
		/*
		 * Reads up to byte.length bytes of data from this input stream into an array of bytes. 
		 */
		while ((bytesRead = input.read(buffer)) != -1) {
			/*
			 * Write read bytes to output stream
			 */
			output.write(buffer, 0, bytesRead);
			/*
			 * Track num of uploaded bytes
			 */
			uploaded += bytesRead;
			
		}
		/*
		 * Get time when upload finish
		 */
		Long endTime = System.currentTimeMillis();
		/*
		 * Calculate duration
		 */
		double timetook = ((double) (endTime - startTime)) / 1000;
		/*
		 * Save time duration for this client
		 */
		Reports.Time.add(String.valueOf(timetook));

		/*
		 * Calculate KB
		 */
		double KBTransfered = uploaded / 1024;

		/*
		 * Save average speed in KB/s for this client
		 */
		Reports.Speed.add(String.valueOf(KBTransfered/timetook));
		

		output.flush();
		/*
		 * Close input and output streams
		 */
		output.close();
		input.close();
	}

	/*
	 * Sends a raw command to the FTP server.
	 */
	
	private void sendLine(String line) throws IOException {
		if (socket == null) {
			throw new IOException("Please connect.");
		}
		try {
			writer.write(line + "\r\n");
			writer.flush();
		} catch (IOException e) {
			socket = null;
			throw e;
		}
	}

	/*
	 * Reads a line of text. In this case response from FTP
	 */
	private String readLine() throws IOException {
		String line = reader.readLine();
		return line;
	}

	private Socket socket = null;

	private BufferedReader reader = null;

	private BufferedWriter writer = null;

}
