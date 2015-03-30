import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Program {

	/*
	 * Method to be started by thread
	 */
	public static void sendFile(String host, String username,
			String password, final File fileName) throws IOException {
		
		File file = fileName;
		final FTPClient client = new FTPClient();
		client.connect(host, username, password);
		client.stor(file);
		client.disconnect();
	}

	/*
	 * Display statistics for each file, and overall
	 */
	public static void showStatistics(int numOfFiles) {
		double totalSpeed = 0;
		double maxTime = 0;
		String output = new String();
		DecimalFormat df = new DecimalFormat("0.00");
		
		for (int k = 0; k < numOfFiles; k++) {			
			/*
			 * For some reason sometimes, not always name is not stored in Reports
			 * and throw a exception so I had to check it first.
			 */
			String name = 
					Reports.Names.get(k) != null ? Reports.Names.get(k) : "File";
			String avgs = 
					Reports.Speed.get(k) != null ? Reports.Speed.get(k) : "0.00";
			String time = 
					Reports.Time.get(k) != null ? Reports.Time.get(k) : "0.00";
			
			double speed = Double.parseDouble(avgs);
			double timenum = Double.parseDouble(time);
			/*
			 * Since You said in one or two rows I had to do it like this
			 */
			output += name + " at " + df.format(speed)
					+ " KB/s, in " + df.format(timenum) + " secs. ";

			/*
			 * Check maximum time of upload
			 */
			totalSpeed += speed;
			if (maxTime < timenum) {
				maxTime = timenum;
			}
		}

		System.out.println(output);

		System.out.println(
				"Average speed for all files: "
				+ df.format(totalSpeed / numOfFiles) 
				+ ". All finished in : " 
				+ df.format(maxTime));
	}


	
	
	public static void startThreads(int numOfFiles, final String server,
			final String username, final String password, ArrayList<File> files) {

		/*
		 * Thread pool
		 */
		ExecutorService executor = Executors.newFixedThreadPool(numOfFiles);

		/*
		 * Array of threads, length -> number of files
		 */
		
		Thread[] tds = new Thread[numOfFiles];

		int i = 0;
		for (final File file : files) {
			tds[i] = new Thread() {
				public void run() {
					try {
						sendFile(server, username, password, file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			executor.execute(tds[i++]);
		}

		executor.shutdown();

		/*
		 * Wait for execution of all threads
		 */
		while (!executor.isTerminated()) {

		}
	}
	
	public static void main(String[] args) throws IOException {
		/*
		 * Parse arguments
		 */
		CmdArgs cmd = new CmdArgs(args);

		final String server = cmd.getServer();
		final String username = cmd.getUsername();
		final String password = cmd.getPassword();
		String[] fileNames = cmd.getFileNames();
				
		/*
		 * check for errors
		 */
		if (cmd.hasErrors() == true) {
			System.exit(0);
		}
		int numOfFiles = fileNames.length;
		
		ArrayList<File> files = new ArrayList<File>();
		/*
		 * if filepath is correct append files ArrayList
		 * I should put this validation in class CmdArgs and implement new logic
		 * but because it would take me time I just left like this
		 * 
		 * -basic validation
		 */
		for (final String fileName : fileNames) {
			try{
				File file = new File(fileName);
				@SuppressWarnings({ "unused", "resource" })
				InputStream inputStream = new FileInputStream(file);
				files.add(file);
			}catch (Exception e) {
				System.out.println(fileName + " : Please specify correct path.");
				numOfFiles--;
			}
		}

		startThreads(numOfFiles, server, username, password, files);
		showStatistics(numOfFiles);
	}
}
